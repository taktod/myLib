/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
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

/**
 * mpegtsのchunkを取り出すための動作マネージャー
 * 基本的にgetChunksにUnitデータ(flvのTagとかmpegtsのPacketとか)をいれると
 * 対応したMediaChunkがでてくる。
 * 
 * TODO このchunkManagerは中途で音声や映像のtrackが追加されることは想定していないので、そういう場合に誤動作する可能性があります。
 * VLCの出力データは得意ではないということですね。
 * audioがpcrになっているデータは想定済みになってます。
 * またVLCの出力データでAC-3を扱ったのだが、myLib.media.ac3をつくらないと対応無理。
 */
public class MpegtsChunkManager extends MediaChunkManager {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsChunkManager.class);
	/** sdtデータ */
	private final Sdt sdt;
	/** patデータ */
	private Pat pat = null;
	/** pmtデータ */
	private Pmt pmt = null;
	/** 処理中のaudioData保持オブジェクト */
	private AudioDataList audioDataList = new AudioDataList();
	/** 処理中のvideoData保持オブジェクト */
	private VideoDataList videoDataList = new VideoDataList();
	/** すでに処理済みのpts値 */
	private long passedPts = 0;
	/** 現在処理中のchunkオブジェクト */
	private MpegtsChunk chunk = null;
	/** 解析用のオブジェクト */
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
	 * <note>
	 * setupTracksを先に設定してください。(設定後のpmtをベースに処理するため)
	 * </note>
	 * @param pesAnalyzer
	 */
	public void addPesAnalyzer(IPesAnalyzer pesAnalyzer) {
		// pmtを設置してやらないと自分がpcrであるかわからない。
		pesAnalyzer.setAudioDataList(audioDataList);
		pesAnalyzer.setVideoDataList(videoDataList);
		pesAnalyzer.analyze(pmt, 0); // pmtを先行して送っておきます。
		analyzers.add(pesAnalyzer);
	}
	/**
	 * トラック情報をいれておきます。(mpegtsの場合はpatやpmtから読み取るので必要ありません。)
	 * あらかじめ宣言したいときにいれておくと、すんなり動作します。
	 * なお、すでに定義済みの場合は例外をなげるようにします。
	 * 再定義したい場合はpmtを作り直してください。
	 */
	public void setupTracks(CodecType videoCodec, CodecType audioCodec) throws Exception {
		if(pat != null || pmt != null || pmt == null || pmt.getFields().size() != 0) {
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
			analyzer.analyze(unit, 0);
		}
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
		// TODO あとで下記変わることがあるので、この点注意
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
			return null;
		}
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
				chunk = makeNewChunk();
				// 開始時の時刻を書き込んでおきたい。
				if(pmt.getPcrPid() == audioDataList.getPid()) { // 音声のpidとpcrPidが一致する場合
					chunk.setTimestamp(audioDataList.getFirstDataPts());
				}
				else { // それ以外の場合は映像を採用します
					chunk.setTimestamp(videoDataList.getFirstDataPts());
				}
			}
			// unitを作成する。
			IMediaChunk resultChunk = makeFrameUnit(targetPts);
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
	 * 動作用のchunkを生成します。
	 * @return
	 */
	protected MpegtsChunk makeNewChunk() throws Exception {
		MpegtsChunk chunk = new MpegtsChunk();
		chunk.write(sdt.getBuffer());
		chunk.write(pat.getBuffer());
		chunk.write(pmt.getBuffer());
		return chunk;
	}
	/**
	 * frameunitを作成します。
	 * ただし、映像のあるなし、音声のあるなしによって変わります。
	 * @param targetPts -1ならすべて
	 */
	private IMediaChunk makeFrameUnit(long targetPts) throws Exception {
		if(videoDataList.getCodecType() == null) {
			// 音声のみの場合
			return makeAudioOnlyFrameUnit(targetPts);
		}
		else if(audioDataList.getCodecType() == null) {
			// 映像のみの場合
			return makeVideoOnlyFrameUnit(targetPts);
		}
		else {
			// 両方ある場合
			return makeNormalFrameUnit(targetPts);
		}
	}
	/**
	 * 音声のみのframeUnitをつくります。
	 * @param targetPts -1ならすべてのデータ
	 * @throws Exception
	 */
	private IMediaChunk makeAudioOnlyFrameUnit(long targetPts) throws Exception {
		int audioSize = 0;
		List<IAudioData> audioList = new ArrayList<IAudioData>();
		long audioStartPts = audioDataList.getFirstDataPts();
		while(true) {
			IAudioData audioData = audioDataList.shift();
			if(audioData == null || (targetPts != -1 && audioDataList.getFirstDataPts() > targetPts)) {
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
	 * 映像のみのframeUnitをつくります
	 * @param targetPts -1ならすべて取り出します
	 * @return
	 */
	private IMediaChunk makeVideoOnlyFrameUnit(long targetPts) throws Exception {
		// pesデータをvideoDataListから引き出していく。
		List<Pes> videoList = new ArrayList<Pes>();
		while(true) {
			Pes videoPes = videoDataList.shift();
			if(videoPes != null && videoPes.isPayloadUnitStart()) {
				makeVideoPes(videoList);
				videoList.clear();
			}
			if(videoPes == null || // もうvideoPesがない場合
					(targetPts != -1 &&
					(videoPes.isAdaptationFieldExist() && videoPes.getAdaptationField().getRandomAccessIndicator() == 1) && // keyFrameで
					(videoPes.hasPts() && videoPes.getPts().getPts() > targetPts))) { // pts値が目標のptsを超えている場合
				if(videoPes != null) {
					videoDataList.unshift(videoPes);
				}
				if(videoList.size() != 0) {
					makeVideoPes(videoList);
					videoList.clear();
				}
				// データが残っている場合は記録しなければいけな・・・いことないか
				long durationTimestamp = videoDataList.getFirstDataPts() - chunk.getTimestamp();
				chunk.setDuration(durationTimestamp / 90000F);
				passedPts = videoDataList.getFirstDataPts();
				break;
			}
			// pesがある場合は書き込んでいく。
			videoList.add(videoPes);
		}
		return chunk;
	}
	/**
	 * 通常のframeUnitを作ります。
	 * @param targetPts -1の場合はすべて引き出します
	 * @return
	 */
	private IMediaChunk makeNormalFrameUnit(long targetPts) throws Exception {
		// pesデータをvideoDataListから引き出していく。
		int audioSize = 0;
		List<IAudioData> audioList = new ArrayList<IAudioData>();
		List<Pes> videoList = new ArrayList<Pes>();
		long audioStartPts = audioDataList.getFirstDataPts();
		// はじめのframeの処理をしたというフラグをいれます。(これをいれないとフレーム0で処理がおわることがあります。)
		boolean firstFlg = true;
		// 開始前のptsは必要ないか？
		while(true) {
			Pes videoPes = videoDataList.shift();
			// payloadstartの段階でaudioデータの挿入を気にかける。
			if(videoPes != null && videoPes.isPayloadUnitStart() && videoPes.hasPts()) {
				// いままでにたまったvideoPesについて書き出す
				makeVideoPes(videoList);
				videoList.clear();
				while(true) {
					IAudioData audioData = audioDataList.shift();
					if(audioData == null) {
						if(targetPts != -1) {
							logger.warn("audioDataがnullでした");
							// TODO どうしてもaudioDataがnullにならないと動作しないといったことが発生したら、対処を考える
							throw new Exception("audioDataがnullになることは想定外としておきます。");
						}
						else {
							break;
						}
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
						}
						break;
					}
					audioSize += audioData.getSize();
					audioList.add(audioData);
				}
			}
			if(videoPes == null || // もうvideoPesがない場合
					(targetPts != -1 &&
					(videoPes.isAdaptationFieldExist() && videoPes.getAdaptationField().getRandomAccessIndicator() == 1))) {// keyFrameである場合
				if(firstFlg && videoPes != null) {
					// はじめのデータである場合はフラグをつけて放置する
					firstFlg = false;
				}
				else {
					// 今回の処理完了時
					if(videoPes != null) {
						videoDataList.unshift(videoPes);
					}
					if(videoList.size() != 0) {
						makeVideoPes(videoList);
						videoList.clear();
					}
					// ここまできたときにaudioデータがのこっている場合
					if(audioSize != 0) {
						makeAudioPes(audioSize, audioList, audioStartPts);
					}
					if(targetPts != -1 && videoDataList.getFirstDataPts() < targetPts) {
						return null;
					}
					// データが残っている場合は記録しなければいけな・・・いことないか
					long durationTimestamp = videoDataList.getFirstDataPts() - chunk.getTimestamp();
					chunk.setDuration(durationTimestamp / 90000F);
					passedPts = videoDataList.getFirstDataPts();
					break;
				}
			}
			// videoPesをためておく。
			videoList.add(videoPes);
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
	protected void makeAudioPes(int audioSize, List<IAudioData> audioList, long audioStartPts) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(audioSize);
		for(IAudioData audioData : audioList) {
			buffer.put(audioData.getRawData());
		}
		buffer.flip();
		Pes audioPes = new Pes(audioDataList.getCodecType(),
				pmt.getPcrPid() == audioDataList.getPid(), // pcrであるかはフラグ次第
				true, // randomAccessは絶対にOK(音声なので)
				audioDataList.getPid(), // pid
				buffer, // 実データ
				audioStartPts); // 開始pts
		do {
			chunk.write(audioPes.getBuffer());
		} while((audioPes = audioPes.nextPes()) != null);
	}
	/**
	 * 動画のデータを書き込む
	 * @param videoList
	 */
	protected void makeVideoPes(List<Pes> videoList) throws Exception {
		for(Pes videoPes : videoList) {
			chunk.write(videoPes.getBuffer());
		}
	}
	/**
	 * 現在処理中のchunkについて応答する。
	 */
	@Override
	public IMediaChunk getCurrentChunk() {
		return chunk;
	}
	/**
	 * 残りデータがある場合はここで応答しなければいけない。
	 */
	@Override
	public IMediaChunk close() {
		// すでにデータが枯渇している場合は応答しない。
		if(videoDataList.getListCount() == 0 && audioDataList.getListCount() == 0) {
			return null;
		}
		try {
			// chunkからデータを作って作成しなおす必要あり。
			if(chunk == null) {
				chunk = makeNewChunk();
				// 開始時の時刻を書き込んでおきたい。
				if(pmt.getPcrPid() == audioDataList.getPid()) { // 音声のpidとpcrPidが一致する場合
					chunk.setTimestamp(audioDataList.getFirstDataPts());
				}
				else { // それ以外の場合は映像を採用します
					chunk.setTimestamp(videoDataList.getFirstDataPts());
				}
			}
			// 残っているデータをすべて投入しておく。
			IMediaChunk resultChunk = makeFrameUnit(-1);
			if(resultChunk != null) {
				chunk = null;
			}
			return resultChunk;
		}
		catch(Exception e) {
			logger.warn("例外が発生しました。", e);
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getExt() {
		return "ts";
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public String getHeaderExt() {
		return "ts";
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getPassedTic() {
		return passedPts;
	}
}
