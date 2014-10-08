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

import com.ttProject.frame.CodecType;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * multi frame for video.
 * @author taktod
 */
public class VideoMultiFrame extends VideoFrame {
	/** framelist */
	private List<IVideoFrame> frameList = new ArrayList<IVideoFrame>();
	/**
	 * add frame.
	 * @param frame
	 * @throws Exception
	 */
	public void addFrame(IVideoFrame frame) throws Exception {
		// width and height will be the same, just override with newer frame.
		setWidth(frame.getWidth());
		setHeight(frame.getHeight());
		if(frameList.size() == 0) {
			// for the timestamp, use the first frame.
			setPts(frame.getPts());
			setTimebase(frame.getTimebase());
			if(frame.isKeyFrame()) {
				setKeyFrame(true);
			}
			setSize(frame.getSize());
		}
		else {
			// if keyframe is found, update.
			if(frame.isKeyFrame()) {
				setKeyFrame(true);
			}
			// datasize is updated by new frame size.
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
	public float getDuration() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
	/**
	 * ref the frame list.
	 * @return
	 */
	public List<IVideoFrame> getFrameList() {
		return new ArrayList<IVideoFrame>(frameList);
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
}
