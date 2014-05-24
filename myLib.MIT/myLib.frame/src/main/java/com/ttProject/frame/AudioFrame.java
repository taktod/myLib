/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

/**
 * 音声フレーム
 * @author taktod
 */
public abstract class AudioFrame extends Frame implements IAudioFrame {
	/** 対象フレームのサンプル数 */
	private int sampleNum = 0;
	/** 対象フレームのサンプルレート(秒間に何サンプルあるか？) */
	private int sampleRate = 1;
	/** チャンネル数 */
	private int channel = 1;
	/** ビット数 */
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
	 * 保持サンプル数設定
	 * @param sampleNum
	 */
	protected void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
	}
	/**
	 * サンプルレート設定
	 * @param sampleRate
	 */
	protected void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	/**
	 * 音声チャンネル数設定
	 * @param channel
	 */
	protected void setChannel(int channel) {
		this.channel = channel;
	}
	/**
	 * 音声buffer bit数設定
	 * @param bit
	 */
	protected void setBit(int bit) {
		this.bit = bit;
	}
}
