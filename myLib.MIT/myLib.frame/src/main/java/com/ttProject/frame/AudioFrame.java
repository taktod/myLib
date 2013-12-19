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
	@Override
	public float getDuration() {
		return 1.0f * getSampleNum() / getSampleRate();
	}
	protected void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
	}
	protected void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	protected void setChannel(int channel) {
		this.channel = channel;
	}
	protected void setBit(int bit) {
		this.bit = bit;
	}
}
