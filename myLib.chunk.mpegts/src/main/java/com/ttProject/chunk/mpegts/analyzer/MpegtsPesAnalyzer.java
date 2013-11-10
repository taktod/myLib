package com.ttProject.chunk.mpegts.analyzer;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.mpegts.AudioDataList;
import com.ttProject.chunk.mpegts.VideoDataList;
import com.ttProject.media.Unit;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.media.mpegts.packet.Pmt;

/**
 * pesの内容から、必要な情報を抜き出してvideoDataListやaudioDataListに設置していくアナライザー
 * @author taktod
 *
 */
public class MpegtsPesAnalyzer implements IPesAnalyzer {
	private final Pmt pmt;
	private VideoDataList videoDataList;
	private AudioDataList audioDataList;
	/**
	 * コンストラクタ
	 * @param pmt
	 * @param videoDataList
	 * @param audioDataList
	 */
	public MpegtsPesAnalyzer(Pmt pmt) {
		this.pmt = pmt;
	}
	/**
	 * audioDataListを設定します
	 */
	@Override
	public void setAudioDataList(AudioDataList audioDataList) {
		this.audioDataList = audioDataList;
	}
	/**
	 * videoDataListを設定します
	 */
	@Override
	public void setVideoDataList(VideoDataList videoDataList) {
		this.videoDataList = videoDataList;
	}
	/**
	 * pesについて調査しなければいけない。
	 * 解析できたら、必要に応じてaudioDataListやvideoDataListにみつけたデータをいれなければならない。
	 */
	@Override
	public void analyze(Unit unit) {
		if(!(unit instanceof Pes)) {
			// pesのみに興味あり
			return;
		}
		// pesの場合はpidを確認して、映像であるか音声であるか確認する。
		return;
	}
}
