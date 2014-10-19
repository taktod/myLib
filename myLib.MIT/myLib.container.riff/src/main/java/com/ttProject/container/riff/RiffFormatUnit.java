/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.type.Strh;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAnalyzer;

/**
 * RiffFmtUnit
 * unit to hold format information.
 * @author taktod
 */
public abstract class RiffFormatUnit extends RiffSizeUnit {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(RiffFormatUnit.class);
	private int scale = -1; // can be sampleNum (deltaTics)
	private int rate = -1; // can be sampleRate for audioFrame (timebase)
	private int sampleSize = 0; // sampleSize for strh(if 0, chunk has only 1 frame. if not zero, chunksize / sampleSaze = # of frame)
	private long passedTic = 0;
	/**
	 * constructor
	 * @param type
	 */
	public RiffFormatUnit(Type type) {
		super(type);
	}
	/**
	 * ref the codecType
	 * @return
	 */
	public abstract CodecType getCodecType();
	/**
	 * ref frame analyzer.
	 * @return
	 * @throws Exception 
	 */
	public abstract IAnalyzer getFrameAnalyzer() throws Exception;
	/**
	 * ref frame size.
	 * @return
	 */
	public abstract int getBlockSize();
	/**
	 * ref the extra information.
	 * @return
	 */
	public abstract ByteBuffer getExtraInfo();
	public void setupStrhInfo(Strh relatedStrh) {
		super.setTimebase(relatedStrh.getRate());
		rate       = relatedStrh.getRate();
		scale      = relatedStrh.getScale();
		sampleSize = relatedStrh.getSampleSize(); // if this value is not 0, chunk for this track can be grouped.
		// like pcm_alaw.
	}
	/**
	 * @param rate
	 */
	public void setRate(int rate) {
		super.setTimebase(rate);
		this.rate = rate;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public boolean isTimeReady() {
		return (scale != -1 && rate != -1);
	}
	/**
	 * ref the next pts target number
	 * @return
	 */
	public long getNextPts(int size) {
		long nextPts = (long)(passedTic * scale);
		if(sampleSize == 0) {
			passedTic ++; // 1tic forward.
		}
		else {
			passedTic += size / sampleSize;
		}
		return nextPts;
	}
}
