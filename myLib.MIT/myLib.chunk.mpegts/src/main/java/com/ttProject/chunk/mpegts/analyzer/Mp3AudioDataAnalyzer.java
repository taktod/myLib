/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.mpegts.analyzer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.IAudioData;
import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.FrameAnalyzer;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.media.mp3.frame.Mp3;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * pesからmp3のIAudioDataを取り出すAnalyzer
 * @author taktod
 */
public class Mp3AudioDataAnalyzer implements IAudioDataAnalyzer {
	/** ロガー */
	private Logger logger = Logger.getLogger(Mp3AudioDataAnalyzer.class);
	/** pesをいったんリストにいれてからpayLoadデータの取得がおわったらmp3Frameに分解しなければいけない。 */
	private List<Pes> pesList = new ArrayList<Pes>();
	/** 処理中のpayloadのpts値 */
	private long lastPtsValue = 0L;
	/**
	 * 処理中のpayloadのpts値参照
	 */
	@Override
	public long getLastPtsValue() {
		return lastPtsValue;
	}
	/**
	 * pesからaudioDataを取り出す
	 */
	@Override
	public List<IAudioData> analyzeAudioData(Pes pes) {
		List<IAudioData> result = null;
		if(pes.isPayloadUnitStart()) {
			// payload開始時には、いままでたまった分を取得します。
			result = getRemainData();
		}
		pesList.add(pes);
		return result;
	}
	/**
	 * たまっているデータを取り出します。
	 */
	@Override
	public List<IAudioData> getRemainData() {
		List<IAudioData> result = null;
		if(pesList.size() != 0) {
			ByteBuffer buffer = ByteBuffer.allocate(188 * pesList.size());
			while(pesList.size() > 0) {
				Pes p = pesList.remove(0);
				if(p.isPayloadUnitStart() && p.hasPts()) {
					lastPtsValue = p.getPts().getPts();
				}
				buffer.put(p.getRawData());
			}
			// 必要なaacがたまった場合
			buffer.flip();
			IReadChannel bufferChannel = new ByteReadChannel(buffer);
			try {
				IFrameAnalyzer analyzer = new FrameAnalyzer();
				Frame frame = null;
				result = new ArrayList<IAudioData>();
				while((frame = analyzer.analyze(bufferChannel)) != null) {
					if(frame instanceof Mp3) {
						result.add((Mp3)frame);
					}
				}
			}
			catch(Exception e) {
				logger.error("例外が発生しました", e);
			}
		}
		return result;
	}
}
