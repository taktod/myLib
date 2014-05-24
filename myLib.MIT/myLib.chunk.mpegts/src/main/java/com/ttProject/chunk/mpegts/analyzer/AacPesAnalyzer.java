/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.mpegts.analyzer;

import org.apache.log4j.Logger;

import com.ttProject.chunk.mpegts.AudioDataList;
import com.ttProject.chunk.mpegts.VideoDataList;
import com.ttProject.media.Unit;
import com.ttProject.media.aac.frame.Aac;

/**
 * aacのframeをpes用に登録します。
 * 映像用のPesはスルー
 * @author taktod
 */
public class AacPesAnalyzer implements IPesAnalyzer {
	/** 動作ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AacPesAnalyzer.class);
	/** audioのデータ保持オブジェクト */
	private AudioDataList audioDataList;
	/**
	 * videoDataListを設定します。
	 */
	@Override
	public void setVideoDataList(VideoDataList videoDataList) {
		// 処理することがないので捨てます
	}
	/**
	 * audioDataListを設定します。
	 */
	@Override
	public void setAudioDataList(AudioDataList audioDataList) {
		this.audioDataList = audioDataList;
	}
	/**
	 * データを登録します。
	 */
	@Override
	public void analyze(Unit unit, long timestamp) {
		if(unit instanceof Aac) {
			// aacの場合はaudioDataListにいれますが、動作中のptsが必要になります。(開始位置の情報を登録する必要があるります。)
			Aac aac = (Aac) unit;
			audioDataList.addAudioData(aac, timestamp);
		}
	}
}
