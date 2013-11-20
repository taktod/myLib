package com.ttProject.chunk.mpegts.analyzer;

import org.apache.log4j.Logger;

import com.ttProject.chunk.mpegts.AudioDataList;
import com.ttProject.chunk.mpegts.VideoDataList;
import com.ttProject.media.Unit;
import com.ttProject.media.mp3.frame.Mp3;

/**
 * mp3のframeをpes用に登録します。
 * 映像用のPesはスルー
 * @author taktod
 */
public class Mp3PesAnalyzer implements IPesAnalyzer {
	/** 動作ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Mp3PesAnalyzer.class);
	/** audioのデータ保持オブジェクト */
	private AudioDataList audioDataList;
	/**
	 * videoDataListを設定します。
	 */
	@Override
	public void setVideoDataList(VideoDataList videoDataList) {
		// やることなし
	}
	/**
	 * audioDataListを設定します。
	 */
	@Override
	public void setAudioDataList(AudioDataList audioDataList) {
		this.audioDataList = audioDataList;
	}
	/**
	 * pmt値が知りたいので登録するようにしておきます。
	 * 開始時のptsはとりあえず0と仮定します。
	 */
	@Override
	public void analyze(Unit unit, long timestamp) {
		if(unit instanceof Mp3) {
			Mp3 mp3 = (Mp3)unit;
			audioDataList.addAudioData(mp3, timestamp);
		}
	}
}
