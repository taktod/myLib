/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;

import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.xuggle.frame.Packetizer;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;

/**
 * いろんなコンテナの動作テストで利用する変換のコアの部分だけ抜き出しておく。
 * @author taktod
 */
public class DecodeBase {
	/** ロガー */
	private Logger logger = Logger.getLogger(DecodeBase.class);
	/** デコード */
	private IStreamCoder decoder = null;
	/** frame -> packet化 */
	private Packetizer packetizer = new Packetizer();
	/** xuggleのpacket */
	private IPacket packet = null;
	/**
	 * 映像データをデコードします
	 * @param frame
	 * @throws Exception
	 */
	public void processVideoDecode(IVideoFrame frame) throws Exception {
		if(frame instanceof VideoMultiFrame) {
			VideoMultiFrame multiFrame = (VideoMultiFrame) frame;
			for(IVideoFrame videoFrame : multiFrame.getFrameList()) {
				processVideoDecode(videoFrame);
			}
			return;
		}
		decoder = packetizer.getDecoder(frame, decoder);
		if(decoder == null) {
			return;
		}
		if(!decoder.isOpen()) {
			if(decoder.open(null, null) < 0) {
				throw new Exception("デコーダーが開けませんでした");
			}
		}
		IPacket pkt = packetizer.getPacket(frame, packet);
		if(pkt == null) {
			return;
		}
		packet = pkt;
		logger.info(packet);
		IVideoPicture picture = IVideoPicture.make(decoder.getPixelType(), frame.getWidth(), frame.getHeight());
		int offset = 0;
		while(offset < packet.getSize()) {
			int bytesDecoded = decoder.decodeVideo(picture, packet, offset);
			if(bytesDecoded <= 0) {
				throw new Exception("データのデコードに失敗しました");
			}
			offset += bytesDecoded;
			if(picture.isComplete()) {
				logger.info(picture);
			}
		}
	}
	/**
	 * 音声データをデコードします
	 * @param frame
	 * @throws Exception
	 */
	public void processAudioDecode(IAudioFrame frame) throws Exception {
		if(frame instanceof AudioMultiFrame) {
			AudioMultiFrame multiFrame = (AudioMultiFrame)frame;
			for(IAudioFrame audioFrame : multiFrame.getFrameList()) {
				processAudioDecode(audioFrame);
			}
			return;
		}
		decoder = packetizer.getDecoder(frame, decoder);
		if(decoder == null) {
			return; // frameがデコーダーに対応していないものもあるので、その場合は次にまわす
		}
		if(!decoder.isOpen()) {
			if(decoder.open(null, null) < 0) {
				throw new Exception("デコーダーが開けません");
			}
		}
		IPacket pkt = packetizer.getPacket(frame, packet);
		if(pkt == null) {
			return;
		}
		packet = pkt;
		logger.info(packet);
		IAudioSamples samples = IAudioSamples.make(1024, decoder.getChannels());
		int offset = 0;
		while(offset < packet.getSize()) {
			int bytesDecoded = decoder.decodeAudio(samples, packet, offset);
			if(bytesDecoded < 0) {
				throw new Exception("データのデコードに失敗しました。");
			}
			offset += bytesDecoded;
			if(samples.isComplete()) {
				logger.info(samples);
			}
		}
	}
	/**
	 * 終了処理
	 */
	public void close() {
		if(decoder != null) {
			decoder.close();
			decoder = null;
		}
	}
}
