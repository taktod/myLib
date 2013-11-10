package com.ttProject.chunk.mpegts;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.MediaChunkManager;
import com.ttProject.chunk.mpegts.analyzer.IPesAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.media.mpegts.packet.Pat;
import com.ttProject.media.mpegts.packet.Pmt;
import com.ttProject.media.mpegts.packet.Sdt;

/**
 * mpegtsのchunkを取り出すための動作マネージャー
 * 基本的にgetChunksにUnitデータ(flvのTagとかmpegtsのPacketとか)をいれると、対応したMediaChunkがでてくる。
 */
public class MpegtsChunkManager extends MediaChunkManager {
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
		else {
			// データをanalyzeしてchunkが取得できたらそこでおわり。
			// それ以外の場合はnullを返す。
			for(IPesAnalyzer analyzer : analyzers) {
				analyzer.analyze(unit);
			}
			// 複数取れる可能性も一応あるのか・・・
			return checkCompleteChunk(); // 完了したchunkについて調査する。
		}
		return null;
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
			return null;
		}
		// 処理したいtimestampを求めておく
		long targetPts = passedPts + (long)(90000 * getDuration());
		// 映像と音声のdurationについて確認しておく。
		// 問題のduration以上データがのこっていることを確認しておく。
		if((videoDataList.getCodecType() != null && videoDataList.getLastDataPts() > targetPts)
		&& (audioDataList.getCodecType() != null && audioDataList.getLastDataPts() > targetPts)) {
			// すでにデータがたまっている。
			// mpegtsChunkにデータをいれていく必要あり。
			if(chunk == null) {
				chunk = new MpegtsChunk();
				chunk.write(sdt.getBuffer());
				chunk.write(pat.getBuffer());
				chunk.write(pmt.getBuffer());
			}
			// unitを作成する。
			// 必要な長さのデータができていたら応答する。
		}
		return null;
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
	 * 処理時間(秒数表記)
	 */
	@Override
	public float getDuration() {
		long videoPts = 0, audioPts = 0;
		if(videoDataList.getCodecType() != null) {
			videoPts = videoDataList.getFirstDataPts();
		}
		if(audioDataList.getCodecType() != null) {
			audioPts = audioDataList.getFirstDataPts();
		}
		if(audioPts > videoPts) {
			return videoPts / 90000f;
		}
		else {
			return audioPts / 90000f;
		}
	}
}
