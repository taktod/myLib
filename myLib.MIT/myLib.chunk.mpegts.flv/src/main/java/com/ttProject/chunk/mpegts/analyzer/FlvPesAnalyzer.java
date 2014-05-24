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
import com.ttProject.media.mpegts.packet.Pmt;

/**
 * flvのデータを解析してpesを作成する動作
 * 音声用の動作は別途作成します。
 * @author taktod
 */
public class FlvPesAnalyzer implements IPesAnalyzer {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(FlvPesAnalyzer.class);
	/** 動作pmt */
	private Pmt pmt = null;
	/** 音声データリスト(IAudioData) */
	private AudioDataList audioDataList = null;
	/** 映像データリスト(Pes) */
	private VideoDataList videoDataList = null;
	/**
	 * 解析動作
	 */
	@Override
	public void analyze(Unit unit, long timestamp) {
		if(unit instanceof Pmt) {
			if(pmt != null) { // すでに解析済みならスルー
				return;
			}
			// pmtの内容を解析します。
			// h264があれば、flvから引き出す候補に
			// mp3やaacがあればこちらもflvから引き出す候補にします。
		}
		else {
			try {
				// flvからデータを引き出します。
				// コーデックがあわなければ引き出し候補から外します。
			}
			catch(Exception e) {
				
			}
		}
	}
	/**
	 * 音声データリスト保持
	 */
	@Override
	public void setAudioDataList(AudioDataList audioDataList) {
		this.audioDataList = audioDataList;
	}
	/**
	 * 映像データリスト保持
	 */
	@Override
	public void setVideoDataList(VideoDataList videoDataList) {
		this.videoDataList = videoDataList;
	}
}
