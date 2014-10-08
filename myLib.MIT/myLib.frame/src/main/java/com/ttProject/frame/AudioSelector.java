/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;
import com.ttProject.unit.ISelector;

/**
 * base for audio selector
 * @author taktod
 * hold the data from container for the default. override with analyzed data.
 */
public abstract class AudioSelector implements ISelector {
	// default from container.
	/** channel */
	private int channel;
	/** bitdepth */
	private int bit;
	/** sampleRate */
	private int sampleRate;
	/** sampleNum */
	private int sampleNum;
	/**
	 * put the default value as much as possible.
	 * @param frame
	 * @return
	 */
	public void setup(AudioFrame frame) {
		frame.setChannel(channel);
		frame.setBit(bit);
		frame.setSampleRate(sampleRate);
		frame.setSampleNum(sampleNum);
	}
	/**
	 * set the channel num.
	 * @param channel
	 */
	public void setChannel(int channel) {
		this.channel = channel;
	}
	/**
	 * set the bit depth
	 * @param bit
	 */
	public void setBit(int bit) {
		this.bit = bit;
	}
	/**
	 * set the sampleRate
	 * @param sampleRate
	 */
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	/**
	 * set the sampleNum
	 * @param sampleNum
	 */
	public void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
	}
}
