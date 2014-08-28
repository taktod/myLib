/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.frameutil;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.Frame;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.speex.SpeexFrameSelector;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.frameutil.AnalyzerChecker;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * packet -> frame変換
 * @author taktod
 */
public class Depacketizer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Depacketizer.class);
	private IAnalyzer analyzer = null;
	private AnalyzerChecker analyzerChecker = new AnalyzerChecker();
	private CodecTypeChecker codecChecker = new CodecTypeChecker();
	/**
	 * packetからframeを取り出す動作
	 * @param encoder
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	public IFrame getFrame(IStreamCoder encoder, IPacket packet) throws Exception {
		// analyzerを取得する必要がある。// 別のデータのanalyzerになってしまうことはないと願いたい。
		CodecType type = codecChecker.getCodecType(encoder.getCodecID());
		if(type.isAudio()) {
			return getAudioFrame(encoder, packet, type);
		}
		else if(type.isVideo()) {
			return getVideoFrame(encoder, packet, type);
		}
		else {
			throw new Exception("データtypeが不正です。");
		}
	}
	/**
	 * 映像の処理
	 * @param encoder
	 * @param packet
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private IFrame getVideoFrame(IStreamCoder encoder, IPacket packet, CodecType type) throws Exception {
		if(analyzer == null || analyzer.getCodecType() != type) {
			analyzer = analyzerChecker.checkAnalyzer(type);
			if(analyzer instanceof AudioAnalyzer) {
				throw new Exception("映像codecのanalyzerが音声のものでした。");
			}
			VideoAnalyzer vAnalyzer = (VideoAnalyzer)analyzer;
			VideoSelector selector = vAnalyzer.getSelector();
			selector.setWidth(encoder.getWidth());
			selector.setHeight(encoder.getHeight());
		}
		VideoMultiFrame result = new VideoMultiFrame();
		IFrame frame = null;
		IReadChannel channel = new ByteReadChannel(packet.getData().getByteArray(0, packet.getSize()));
		while((frame = analyzer.analyze(channel)) != null) {
			Frame f = (Frame) frame;
			f.setPts(packet.getPts());
			result.addFrame((IVideoFrame)frame);
		}
		frame = analyzer.getRemainFrame();
		if(frame != null) {
			Frame f = (Frame) frame;
			f.setPts(packet.getPts());
			result.addFrame((IVideoFrame)frame);
		}
		switch(result.getFrameList().size()) {
		case 0:
			return null;
		case 1:
			return result.getFrameList().get(0);
		default:
			return result;
		}
	}
	/**
	 * 音声の処理
	 * @param decoder
	 * @param packet
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private IFrame getAudioFrame(IStreamCoder encoder, IPacket packet, CodecType type) throws Exception {
		if(analyzer == null || analyzer.getCodecType() != type) {
			analyzer = analyzerChecker.checkAnalyzer(type);
			if(analyzer instanceof VideoAnalyzer) {
				throw new Exception("音声codecのanalyzerが映像のものでした。");
			}
			AudioAnalyzer aAnalyzer = (AudioAnalyzer)analyzer;
			AudioSelector selector = aAnalyzer.getSelector();
			selector.setChannel(encoder.getChannels());
			selector.setSampleRate(encoder.getSampleRate());
			// vorbisの場合は、privateDataを通しておく必要があると思う
			IFrame extraFrame = null;
			switch(type) {
			case VORBIS:
				{
					IReadChannel privateData = new ByteReadChannel(encoder.getExtraData().getByteBuffer(0, encoder.getExtraDataSize()));
					logger.info(HexUtil.toHex(encoder.getExtraData().getByteBuffer(0, encoder.getExtraDataSize())));
				}
				break;
			case SPEEX:
				{
					IReadChannel privateData = new ByteReadChannel(encoder.getExtraData().getByteBuffer(0, encoder.getExtraDataSize()));
					// どうやらheaderの部分の量のみっぽいです。commentはないのか？
					CommentFrame commentFrame = new CommentFrame();
					while((extraFrame = analyzer.analyze(privateData)) != null) {
						if(extraFrame instanceof HeaderFrame) {
							commentFrame.setHeaderFrame((HeaderFrame)extraFrame);
						}
					}
					commentFrame.setVenderName("ttProject");
					commentFrame.addElement("Depacketizer");
					SpeexFrameSelector sselector = (SpeexFrameSelector)selector;
					sselector.setCommentFrame(commentFrame);
				}
				break;
			default:
				break;
			}
		}
		AudioMultiFrame result = new AudioMultiFrame();
		IFrame frame = null;
		IReadChannel channel = new ByteReadChannel(packet.getData().getByteArray(0, packet.getSize()));
		while((frame = analyzer.analyze(channel)) != null) {
			Frame f = (Frame) frame;
			f.setPts(packet.getPts());
			result.addFrame((IAudioFrame)frame);
		}
		frame = analyzer.getRemainFrame();
		if(frame != null) {
			Frame f = (Frame) frame;
			f.setPts(packet.getPts());
			result.addFrame((IAudioFrame)frame);
		}
		switch(result.getFrameList().size()) {
		case 0:
			return null;
		case 1:
			return result.getFrameList().get(0);
		default:
			return result;
		}
	}
}
