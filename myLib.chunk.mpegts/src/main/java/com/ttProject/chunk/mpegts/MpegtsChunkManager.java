package com.ttProject.chunk.mpegts;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.MediaChunkManager;
import com.ttProject.chunk.mpegts.analyzer.IPesAnalyzer;
import com.ttProject.media.IAudioData;
import com.ttProject.media.Unit;
import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.media.mpegts.packet.Pat;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.media.mpegts.packet.Pmt;
import com.ttProject.media.mpegts.packet.Sdt;
import com.ttProject.util.HexUtil;

/**
 * mpegtsのchunkを取り出すための動作マネージャー
 * 基本的にgetChunksにUnitデータ(flvのTagとかmpegtsのPacketとか)をいれると、対応したMediaChunkがでてくる。
 */
public class MpegtsChunkManager extends MediaChunkManager {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsChunkManager.class);
	/** sdtデータ */
	private final Sdt sdt;
	/** patデータ */
	private Pat pat;
	/** pmtデータ */
	private Pmt pmt;
	/** 処理中のaudioData保持オブジェクト */
	private AudioDataList audioDataList = new AudioDataList();
	/** 処理中のvideoData保持オブジェクト */
	private VideoDataList videoDataList = new VideoDataList();
	/** すでに処理済みのpts値 */
	private long passedPts = 0;
	/** 現在処理中のchunkオブジェクト */
	private MpegtsChunk chunk = null;
	/**
	 * 解析用のオブジェクト
	 */
	private Set<IPesAnalyzer> analyzers = new HashSet<IPesAnalyzer>();
	/**
	 * コンストラクタ
	 * @throws Exception
	 */
	public MpegtsChunkManager() throws Exception {
		sdt = new Sdt();
		sdt.writeDefaultProvider("taktodTools", "mpegtsChunkMuxer");
	}
	/**
	 * pesの解析オブジェクトを登録する。
	 * @param pesAnalyzer
	 */
	public void addPesAnalyzer(IPesAnalyzer pesAnalyzer) {
		pesAnalyzer.setAudioDataList(audioDataList);
		pesAnalyzer.setVideoDataList(videoDataList);
		analyzers.add(pesAnalyzer);
	}
	/**
	 * トラック情報をいれておきます。(mpegtsの場合はpatやpmtから読み取るので必要ありません。)
	 * あらかじめ宣言したいときにいれておくと、すんなり動作します。
	 * なお、すでに定義済みの場合は例外をなげるようにします。
	 * 再定義したい場合はpmtを作り直してください。
	 */
	public void setupTracks(CodecType videoCodec, CodecType audioCodec) throws Exception {
		if(pat != null || pmt != null || pmt.getFields().size() != 0) {
			throw new Exception("すでにfield定義がおわっています。");
		}
		Pat pat = new Pat();
		analyzePat(pat);
		Pmt pmt = new Pmt();
		if(videoCodec != null) {
			pmt.addNewField(PmtElementaryField.makeNewField(videoCodec));
		}
		if(audioCodec != null) {
			pmt.addNewField(PmtElementaryField.makeNewField(audioCodec));
		}
		analyzePmt(pmt);
	}
	/**
	 * chunkを取り出します。
	 */
	@Override
	public IMediaChunk getChunk(Unit unit) throws Exception {
		// 映像データの場合は、mpegtsのpesに変更しなければいけない。
		// 音声データの場合は、IAudioDataに変更しなければいけない。
		if(unit instanceof Pat) {
			// mpegtsのpatの場合
			analyzePat((Pat) unit);
		}
		else if(unit instanceof Pmt) {
			// mpegtsのpmtの場合
			analyzePmt((Pmt) unit);
		}
		// データをanalyzeしてchunkが取得できたらそこでおわり。
		// それ以外の場合はnullを返す。
		for(IPesAnalyzer analyzer : analyzers) {
			analyzer.analyze(unit);
		}
		// 複数取れる可能性も一応あるのか・・・
		return checkCompleteChunk(); // 完了したchunkについて調査する。
	}
	/**
	 * patを解析します
	 * @param pat
	 */
	private void analyzePat(Pat pat) {
		if(this.pat != null) {
			return;
		}
		this.pat = pat;
	}
	/**
	 * pmtを解析します
	 * @param pmt
	 */
	private void analyzePmt(Pmt pmt) throws Exception {
		if(this.pmt != null) {
			return;
		}
		this.pmt = pmt;
		for(PmtElementaryField field : pmt.getFields()) {
			switch(field.getCodecType()) {
			case VIDEO_H264:
				videoDataList.analyzePmt(pmt, field);
				break;
			case AUDIO_AAC:
			case AUDIO_MPEG1:
				audioDataList.analyzePmt(pmt, field);
				break;
			default:
				break;
			}
		}
	}
	/**
	 * chunkが作成完了したか確認する。
	 * @return
	 */
	private IMediaChunk checkCompleteChunk() throws Exception {
		// 先頭情報が抜け落ちている場合は処理できない。
		if(sdt == null || pat == null || pmt == null) {
			logger.info("必要なセットアップ情報がありませんでした。");
			return null;
		}
		IMediaChunk resultChunk;
		// 処理したいtimestampを求めておく
		long targetPts = passedPts + (long)(90000 * getDuration());
		// 映像と音声のdurationについて確認しておく。
		// 問題のduration以上データがのこっていることを確認しておく。
		if((videoDataList.getCodecType() == null || (videoDataList.getCodecType() != null && videoDataList.getLastDataPts() > targetPts))
		&& (audioDataList.getCodecType() == null || (audioDataList.getCodecType() != null && audioDataList.getLastDataPts() > targetPts))) {
			// 映像音声ともにあるデータの場合は、keyFrame間の音声データが満了しているか確認しておく。
			if(videoDataList.getCodecType() != null && audioDataList.getCodecType() != null // 両方メディアがある
			&& videoDataList.getSecondDataPts() != -1 // videoDataのkeyFrameが２つ以上内包されている
			&& videoDataList.getSecondDataPts() > audioDataList.getLastDataPts()) { // ２つ目までのaudioデータがcompleteしている
				// 満了していなさそうなので、処理をスキップ
				return null;
			}
			// すでにデータがたまっている。
			// mpegtsChunkにデータをいれていく必要あり。
			if(chunk == null) {
				logger.info("chunkがないので、新規生成します。");
				chunk = new MpegtsChunk();
				chunk.write(sdt.getBuffer());
				chunk.write(pat.getBuffer());
				chunk.write(pmt.getBuffer());
				// 開始時の時刻を書き込んでおきたい。
				if(videoDataList.getCodecType() != null) {
					chunk.setTimestamp(videoDataList.getFirstDataPts());
				}
				else if(audioDataList.getCodecType() != null) {
					chunk.setTimestamp(audioDataList.getFirstDataPts());
				}
			}
			// unitを作成する。
			resultChunk = makeFrameUnit(targetPts);
			if(resultChunk != null) {
				// 前のデータが完成しているので、次のデータにうつりたい。
				chunk = null;
			}
			// 必要な長さのデータができていたら応答する。
			return resultChunk;
		}
		return null;
	}
	/**
	 * frameunitを作成します。
	 * ただし、映像のあるなし、音声のあるなしによって変わります。
	 * @param targetPts
	 */
	private IMediaChunk makeFrameUnit(long targetPts) throws Exception {
		if(videoDataList.getCodecType() == null) {
			// 音声のみの場合
			logger.info("音声のみ");
			return makeAudioOnlyFrameUnit(targetPts);
		}
		else if(audioDataList.getCodecType() == null) {
			// 映像のみの場合
			logger.info("映像のみ");
			return makeVideoOnlyFrameUnit(targetPts);
		}
		else {
			// 両方ある場合
			logger.info("両方ある");
			return makeNormalFrameUnit(targetPts);
		}
	}
	/**
	 * 音声のみのframeUnitをつくります。
	 * @param targetPts
	 * @throws Exception
	 */
	private IMediaChunk makeAudioOnlyFrameUnit(long targetPts) throws Exception {
		int audioSize = 0;
		List<IAudioData> audioList = new ArrayList<IAudioData>();
		long audioStartPts = audioDataList.getFirstDataPts();
		while(true) {
			IAudioData audioData = audioDataList.shift();
			if(audioData == null || audioDataList.getFirstDataPts() > targetPts) {
				// データがなくなった場合もしくは、データが問題のptsを超えた場合
				if(audioData != null) {
					audioDataList.unshift(audioData);
				}
				if(audioSize != 0) {
					// 処理おわり ここまでこれたということはchunkができたということ。
					makeAudioPes(audioSize, audioList, audioStartPts);
				}
				// getFirstDataPtsでデータがなくても音声に限っていえばduration値を応答することは可能。
				long durationTimestamp = audioDataList.getFirstDataPts() - chunk.getTimestamp();
				chunk.setDuration(durationTimestamp / 90000F);
				passedPts = audioDataList.getFirstDataPts();
				break;
			}
			audioSize += audioData.getSize(); // データサイズを計算
			audioList.add(audioData); // データを追加リストに登録
			// ある程度以上データがたまっていたら追加計算しておく。
			if(audioSize > 0x1000) {
				// 書き込み実行
				makeAudioPes(audioSize, audioList, audioStartPts);
				audioList.clear();
				audioSize = 0;
				audioStartPts = audioDataList.getFirstDataPts();
			}
		}
		return chunk;
	}
	/**
	 * audio用のpesを作成します。
	 * @param audioSize
	 * @param audioDataList
	 * @param audioStartPts
	 * @throws Exception
	 */
	private void makeAudioPes(int audioSize, List<IAudioData> audioList, long audioStartPts) throws Exception {
		logger.info("buffer作成");
		ByteBuffer buffer = ByteBuffer.allocate(audioSize);
		for(IAudioData audioData : audioList) {
			buffer.put(audioData.getRawData());
		}
		buffer.flip();
		logger.info("pes作成");
		Pes audioPes = new Pes(audioDataList.getCodecType(),
				pmt.getPcrPid() == audioDataList.getPid(), // pcrであるかはフラグ次第
				true, // randomAccessは絶対にOK(音声なので)
				audioDataList.getPid(), // pid
				buffer, // 実データ
				audioStartPts); // 開始pts
		do {
			buffer = audioPes.getBuffer();
			logger.info(HexUtil.toHex(buffer, 0, 50, true));
			chunk.write(buffer);
		} while((audioPes = audioPes.nextPes()) != null);
	}
	/**
	 * 映像のみのframeUnitをつくります
	 * @param targetPts
	 * @return
	 */
	private IMediaChunk makeVideoOnlyFrameUnit(long targetPts) throws Exception {
		// pesデータをvideoDataListから引き出していく。
		while(true) {
			Pes videoPes = videoDataList.shift();
			if(videoPes == null || // もうvideoPesがない場合
					(videoPes.isAdaptationFieldExist() && videoPes.getAdaptationField().getRandomAccessIndicator() == 1) && // keyFrameで
					(videoPes.hasPts() && videoPes.getPts().getPts() > targetPts)) { // pts値が目標のptsを超えている場合
				if(videoPes != null) {
					videoDataList.unshift(videoPes);
				}
				// データが残っている場合は記録しなければいけな・・・いことないか
				long durationTimestamp = videoDataList.getFirstDataPts() - chunk.getTimestamp();
				chunk.setDuration(durationTimestamp / 90000F);
				passedPts = videoDataList.getFirstDataPts();
				break;
			}
			// pesがある場合は書き込んでいく。
			chunk.write(videoPes.getBuffer());
		}
		return chunk;
	}
	/**
	 * 通常のframeUnitを作ります。
	 * @param targetPts
	 * @return
	 */
	private IMediaChunk makeNormalFrameUnit(long targetPts) throws Exception {
		// pesデータをvideoDataListから引き出していく。
		int audioSize = 0;
		List<IAudioData> audioList = new ArrayList<IAudioData>();
		long audioStartPts = audioDataList.getFirstDataPts();
		// はじめのframeの処理をしたというフラグをいれます。(これをいれないとフレーム0で処理がおわることがあります。)
		boolean firstFlg = true;
		// 開始前のptsは必要ないか？
		while(true) {
			Pes videoPes = videoDataList.shift();
			// payloadstartの段階でaudioデータの挿入を気にかける。
			if(videoPes.isPayloadUnitStart() && videoPes.hasPts()) {
				while(true) {
					IAudioData audioData = audioDataList.shift();
					if(audioData == null) {
						logger.warn("audioDataがnullでした");
						// TODO どうしてもaudioDataがnullにならないと動作しないといったことが発生したら、対処を考える
						throw new Exception("audioDataがnullになることは想定外としておきます。");
					}
					if(audioDataList.getFirstDataPts() > videoPes.getPts().getPts()) {
						// 現在処理中の映像ptsを超えた場合
						if(audioData != null) {
							audioDataList.unshift(audioData);
						}
						// ptsを超えた場合もしくはaudioDataがnullの場合
						if(audioSize > 0x1000) {
							makeAudioPes(audioSize, audioList, audioStartPts);
							audioList.clear();
							audioSize = 0;
							audioStartPts = audioDataList.getFirstDataPts();
							logger.info("書き込みおわり");
						}
						break;
					}
					audioSize += audioData.getSize();
					audioList.add(audioData);
				}
			}
			if(videoPes == null || // もうvideoPesがない場合
					(videoPes.isAdaptationFieldExist() && videoPes.getAdaptationField().getRandomAccessIndicator() == 1)) {// keyFrameである場合
				if(firstFlg && videoPes != null) {
					// はじめのデータである場合はフラグをつけて放置する
					firstFlg = false;
				}
				else {
					// 今回の処理完了時
					if(videoPes != null) {
						videoDataList.unshift(videoPes);
					}
					// ここまできたときにaudioデータがのこっている場合
					if(audioSize != 0) {
						makeAudioPes(audioSize, audioList, audioStartPts);
					}
					if(videoDataList.getFirstDataPts() < targetPts) {
						return null;
					}
					// データが残っている場合は記録しなければいけな・・・いことないか
					long durationTimestamp = videoDataList.getFirstDataPts() - chunk.getTimestamp();
					chunk.setDuration(durationTimestamp / 90000F);
					passedPts = videoDataList.getFirstDataPts();
					break;
				}
			}
			// pesがある場合は書き込んでいく。
			chunk.write(videoPes.getBuffer());
		}
		return chunk;
	}
	/**
	 * 現在処理中のchunkについて応答する。
	 */
	@Override
	public IMediaChunk getCurrentChunk() {
		return null;
	}
	/**
	 * 残りデータがある場合はここで応答しなければいけない。
	 */
	@Override
	public IMediaChunk close() {
		return null;
	}
	/**
	 * 拡張子応答
	 */
	@Override
	public String getExt() {
		return "ts";
	}
	/**
	 * mpegtsの分割では、headerという概念が存在しない
	 */
	@Override
	@Deprecated
	public String getHeaderExt() {
		return "ts";
	}
	/**
	 * 経過ptsを応答します。
	 */
	@Override
	public long getPassedTic() {
		return passedPts;
	}
}
