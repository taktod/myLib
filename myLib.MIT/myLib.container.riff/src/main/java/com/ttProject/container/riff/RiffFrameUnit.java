/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import java.nio.ByteBuffer;

import com.ttProject.frame.Frame;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * riffFrameUnit
 * base for frame unit.
 * @author taktod
 */
public abstract class RiffFrameUnit extends RiffSizeUnit {
	private final int trackId;
	/** format information */
	private RiffFormatUnit formatUnit = null;
	/** frameBuffer is holded on the memory */
	private ByteBuffer frameBuffer = null;
	/**
	 * constructor
	 * @param typeValue
	 * @param type
	 */
	public RiffFrameUnit(int dataValue, Type type) {
		super(type);
		byte[] dat = new byte[2];
		dat[0] = (byte)((dataValue >> 24) & 0xFF);
		dat[1] = (byte)((dataValue >> 16) & 0xFF);
		trackId = Integer.parseInt(new String(dat).intern());
	}
	/**
	 * ref the trackId
	 * @return
	 */
	public int getTrackId() {
		return trackId;
	}
	/**
	 * set the format unit.
	 * should I use public for this?
	 * cause of package, now this can be protected.
	 * @param formatUnit
	 */
	protected void setFormatUnit(RiffFormatUnit formatUnit) {
		this.formatUnit = formatUnit;
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		// load the buffer on the memory
		frameBuffer = BufferUtil.safeRead(channel, getSize() - 8);
		if(!formatUnit.isTimeReady()) {
			IFrame frame = getFrame(); // ref the frame and update data.
			if(!(frame instanceof IAudioFrame)) {
				throw new Exception("formatUnit doesn't have any tic information for videoFrame.");
			}
			// update with audioFrame information for tics.
			IAudioFrame aFrame = (IAudioFrame)frame;
			formatUnit.setScale(aFrame.getSampleNum());
			formatUnit.setRate(aFrame.getSampleRate());
		}
		setPts(formatUnit.getNextPts());
		setTimebase(formatUnit.getTimebase());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public void load(IReadChannel channel) throws Exception {
		// don't use, for wav file, data obj uses extra IReadChannel on Selector.
		// however if we use load func, IReadChannel can be original one.
	}
	/**
	 * get the frame
	 * @return
	 */
	public IFrame getFrame() throws Exception {
		IReadChannel frameChannel = new ByteReadChannel(frameBuffer);
		IAnalyzer analyzer = formatUnit.getFrameAnalyzer();
		IFrame frame = null;
		IFrame resultFrame = null;
		// TODO for multiFrame need to proceed the pts.
		// just now, share the same pts for all frame on frame unit.
		while((frame = analyzer.analyze(frameChannel)) != null) {
			if(!(frame instanceof NullFrame)) {
				resultFrame = processFrame(resultFrame, frame);
			}
		}
		resultFrame = processFrame(resultFrame, analyzer.getRemainFrame());
		return resultFrame;
	}
	private IFrame processFrame(IFrame resultFrame, IFrame frame) throws Exception {
		if(frame == null) {
			return resultFrame;
		}
		Frame f = (Frame)frame;
		f.setPts(getPts());
		f.setTimebase(getTimebase());
		if(resultFrame == null) {
			return frame;
		}
		else if(frame instanceof IAudioFrame) {
			IAudioFrame aFrame = (IAudioFrame)frame;
			if(resultFrame instanceof AudioMultiFrame) {
				((AudioMultiFrame) resultFrame).addFrame(aFrame);
			}
			else if(resultFrame instanceof IAudioFrame) {
				AudioMultiFrame multiFrame = new AudioMultiFrame();
				multiFrame.addFrame((IAudioFrame)resultFrame);
				multiFrame.addFrame(aFrame);
				resultFrame = multiFrame;
			}
			return resultFrame;
		}
		else if(frame instanceof IVideoFrame) {
			IVideoFrame vFrame = (IVideoFrame)frame;
			if(resultFrame instanceof VideoMultiFrame) {
				((VideoMultiFrame) resultFrame).addFrame(vFrame);
			}
			else if(resultFrame instanceof IVideoFrame) {
				VideoMultiFrame multiFrame = new VideoMultiFrame();
				multiFrame.addFrame((IVideoFrame)resultFrame);
				multiFrame.addFrame(vFrame);
				resultFrame = multiFrame;
			}
			return resultFrame;
		}
		else {
			throw new Exception("neither Audio nor Video frame.");
		}
	}
}
