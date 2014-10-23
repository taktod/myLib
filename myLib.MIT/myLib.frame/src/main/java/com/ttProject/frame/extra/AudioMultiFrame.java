/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.extra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * multiFrame for audioFrame.
 * some kind of frame can be treat as one frame.
 * ex: nellymoser on flv, 
 * @author taktod
 */
public class AudioMultiFrame extends AudioFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AudioMultiFrame.class);
	/** frameList */
	private List<IAudioFrame> frameList = new ArrayList<IAudioFrame>();
	/**
	 * add frame
	 * @param frame
	 * @throws Exception
	 */
	public void addFrame(IAudioFrame frame) throws Exception {
		if(frame == null) {
			return;
		}
		if(frameList.size() == 0) {
			setBit(frame.getBit());
			setChannel(frame.getChannel());
			setPts(frame.getPts());
			setTimebase(frame.getTimebase());
			setSampleRate(frame.getSampleRate());
			setSampleNum(frame.getSampleNum());
			setSize(frame.getSize());
		}
		else {
			// just ignore if the format is different.
			setSampleNum(frame.getSampleNum() + getSampleNum()); // samplenum is added.
			setSize(frame.getSize() + getSize());
		}
		frameList.add(frame);
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	protected void requestUpdate() throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public ByteBuffer getPackBuffer() {
		throw new RuntimeException("multiFrame is not support packBuffer.");
	}
	/**
	 * ref the framelist.
	 * @return
	 */
	public List<IAudioFrame> getFrameList() {
		return new ArrayList<IAudioFrame>(frameList);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		if(frameList.size() != 0) {
			return frameList.get(0).getCodecType();
		}
		return CodecType.NONE;
	}
	@Override
	public void setPts(long pts) {
		super.setPts(pts);
		if(getTimebase() == 0) {
			return;
		}
		calcurateFramePts();
	}
	@Override
	public void setTimebase(long timebase) {
		super.setTimebase(timebase);
		calcurateFramePts();
	}
	private void calcurateFramePts() {
		long passedTic = 0;
		int sampleRate = 0;
		long timebase = getTimebase();
		for(IAudioFrame aFrame : frameList) {
			AudioFrame af = (AudioFrame)aFrame;
			af.setTimebase(timebase);
			sampleRate = af.getSampleRate();
			af.setPts(getPts() + passedTic * timebase / sampleRate);
			passedTic += af.getSampleNum();
		}
	}
}
