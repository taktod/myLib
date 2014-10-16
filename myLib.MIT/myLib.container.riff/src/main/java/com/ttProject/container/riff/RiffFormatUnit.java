/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import java.nio.ByteBuffer;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAnalyzer;

/**
 * RiffFmtUnit
 * unit to hold format information.
 * @author taktod
 */
public abstract class RiffFormatUnit extends RiffSizeUnit {
	private int scale = -1; // can be sampleNum (deltaTics)
	private int rate = -1; // can be sampleRate for audioFrame (timebase)
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
	 */
	public abstract IAnalyzer getFrameAnalyzer();
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
	/**
	 * @param rate
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}
	public void setScale(int scale) {
		super.setTimebase(scale);
		this.scale = scale;
	}
	public boolean isTimeReady() {
		return (scale != -1 && rate != -1);
	}
	/**
	 * ref the next pts target number
	 * @return
	 */
	public long getNextPts() {
		long nextPts = (long)(passedTic * scale);
		passedTic ++; // 1tic forward.
		return nextPts;
	}
}
