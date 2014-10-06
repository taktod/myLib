/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;

/**
 * flv Tag Writer
 * @author taktod
 */
public class FlvTagWriter implements IWriter {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(FlvTagWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;

	/** convert frame to flvTag */
	private FrameToFlvTagConverter frameConverter = new FrameToFlvTagConverter();
	public FlvTagWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public FlvTagWriter(FileOutputStream fileOutputStream) {
		this.outputChannel = fileOutputStream.getChannel();
	}
	public FlvTagWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	@Override
	public void addContainer(IContainer container) throws Exception {
		outputChannel.write(container.getData());
	}
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		if(frame == null) {
			return;
		}
		if(frame instanceof VideoMultiFrame) {
			VideoMultiFrame multiFrame = (VideoMultiFrame)frame;
			for(IVideoFrame vFrame : multiFrame.getFrameList()) {
				addFrame(trackId, vFrame);
			}
			return;
		}
		// nellymoser can have multiAudioFrame.
		// however, for mp3, we need to devide for each mp3 frame for audioTag.
		if(frame instanceof AudioMultiFrame) {
			AudioMultiFrame multiFrame = (AudioMultiFrame)frame;
			for(IAudioFrame aFrame : multiFrame.getFrameList()) {
				addFrame(trackId, aFrame);
			}
			return;
		}
		// TODO for h264, videoTag can have multiVideoFrame.
		List<FlvTag> tagList = frameConverter.getTags(frame);
		if(tagList != null) {
			for(FlvTag tag : tagList) {
				outputChannel.write(tag.getData());
			}
		}
	}
	@Override
	public void prepareHeader(CodecType... codecs) throws Exception {
		if(codecs.length == 0) {
			return;
		}
		FlvHeaderTag headerTag = new FlvHeaderTag();
		for(CodecType codec : codecs) {
			if(codec.isAudio()) {
				headerTag.setAudioFlag(true);
			}
			else if(codec.isVideo()) {
				headerTag.setVideoFlag(true);
			}
		}
		addContainer(headerTag);
	}
	@Override
	public void prepareTailer() throws Exception {
		AudioTag audioTag = frameConverter.getRemainAudioTag();
		if(audioTag != null) {
			outputChannel.write(audioTag.getData());
		}
		VideoTag videoTag = frameConverter.getRemainVideoTag();
		if(videoTag != null) {
			outputChannel.write(videoTag.getData());
		}
		// for h264, it is better to put h264 end videoTag.
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {
			}
			outputStream = null;
		}
	}
}
