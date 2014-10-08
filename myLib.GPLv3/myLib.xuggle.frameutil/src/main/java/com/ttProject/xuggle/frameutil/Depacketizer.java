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
import com.ttProject.frame.vorbis.VorbisFrameAnalyzer;
import com.ttProject.frameutil.AnalyzerChecker;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * packet -> frame
 * @author taktod
 */
public class Depacketizer {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Depacketizer.class);
	private IAnalyzer analyzer = null;
	private AnalyzerChecker analyzerChecker = new AnalyzerChecker();
	private CodecTypeChecker codecChecker = new CodecTypeChecker();
	/**
	 * get the frame from packet.
	 * @param encoder
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	public IFrame getFrame(IStreamCoder encoder, IPacket packet) throws Exception {
		CodecType type = codecChecker.getCodecType(encoder.getCodecID());
		if(type.isAudio()) {
			return getAudioFrame(encoder, packet, type);
		}
		else if(type.isVideo()) {
			return getVideoFrame(encoder, packet, type);
		}
		else {
			throw new Exception("invalid data type.");
		}
	}
	/**
	 * video frame
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
				throw new Exception("audioAnalyzer is applyed for videoFrame.");
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
	 * audio frame
	 * @param encoder
	 * @param packet
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private IFrame getAudioFrame(IStreamCoder encoder, IPacket packet, CodecType type) throws Exception {
		if(analyzer == null || analyzer.getCodecType() != type) {
			analyzer = analyzerChecker.checkAnalyzer(type);
			if(analyzer instanceof VideoAnalyzer) {
				throw new Exception("videoAnalyzer is applyed for audioFrame.");
			}
			AudioAnalyzer aAnalyzer = (AudioAnalyzer)analyzer;
			AudioSelector selector = aAnalyzer.getSelector();
			selector.setChannel(encoder.getChannels());
			selector.setSampleRate(encoder.getSampleRate());
			// for vorbis need to set the private data.
			IFrame extraFrame = null;
			switch(type) {
			case VORBIS:
				{
					((VorbisFrameAnalyzer)analyzer).setPrivateData(new ByteReadChannel(encoder.getExtraData().getByteBuffer(0, encoder.getExtraDataSize())));
				}
				break;
			case SPEEX:
				{
					IReadChannel privateData = new ByteReadChannel(encoder.getExtraData().getByteBuffer(0, encoder.getExtraDataSize()));
					// for speex, privateData has only header. no comment frame?
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
			f.setTimebase((long)(1 / packet.getTimeBase().getDouble()));
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
