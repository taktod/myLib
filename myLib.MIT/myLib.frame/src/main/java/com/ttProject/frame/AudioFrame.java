/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

/**
 * base of audioFrame
 * @author taktod
 */
public abstract class AudioFrame extends Frame implements IAudioFrame {
	/** sample num for target frame. */
	private int sampleNum = 0;
	/** sampleRatee */
	private int sampleRate = 1;
	/** channelNum */
	private int channel = 1;
	/** bit depth */
	private int bit = 16;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSampleNum() {
		return sampleNum;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSampleRate() {
		return sampleRate;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChannel() {
		return channel;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBit() {
		return bit;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDuration() {
		return 1.0f * getSampleNum() / getSampleRate();
	}
	/**
	 * set the sampleNum
	 * @param sampleNum
	 */
	protected void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
	}
	/**
	 * set the sampleRate
	 * @param sampleRate
	 */
	protected void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	/**
	 * set the channel
	 * @param channel
	 */
	protected void setChannel(int channel) {
		this.channel = channel;
	}
	/**
	 * set the bit depth
	 * @param bit
	 */
	protected void setBit(int bit) {
		this.bit = bit;
	}
}
