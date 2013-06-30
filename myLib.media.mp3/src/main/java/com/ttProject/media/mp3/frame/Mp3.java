package com.ttProject.media.mp3.frame;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.mp3.Frame;
import com.ttProject.nio.channels.IFileReadChannel;

public class Mp3 extends Frame {
	private final byte mpegVersion; // 2:mpeg2.5(unofficial) 2:mpeg2 3:mpeg1
	private final byte layer; // 2:layer3 1:layer2 0:layer1
	private final int bitrate;// kbps
	private final float samplingRate; // kHz
	private final byte paddingBit;
	private final byte channelMode; // 0:stereo 1:joint stereo 2:Dual channel(2mono channels) 3:monoral
	private final int frameCount;
	public Mp3(int position, int size,
		byte mpegVersion, byte layer, int bitrate, float samplingRate, byte paddingBit, byte channelMode, int frameCount) {
		super(position, size);
		this.mpegVersion = mpegVersion;
		this.layer = layer;
		this.bitrate = bitrate;
		this.samplingRate = samplingRate;
		this.paddingBit = paddingBit;
		this.channelMode = channelMode;
		this.frameCount = frameCount;
	}
	public byte getMpegVersion() {
		return mpegVersion;
	}
	public byte getLayer() {
		return layer;
	}
	public int getBitrate() {
		return bitrate;
	}
	public float getSamplingRate() {
		return samplingRate;
	}
	public byte getPaddingBit() {
		return paddingBit;
	}
	public byte getChannelMode() {
		return channelMode;
	}
	/**
	 * このフレームの始まる時間情報(sec)
	 * @return
	 */
	public float getTime() {
		return getTime(frameCount) / 1000.0f;
	}
	/**
	 * このフレームの時間情報(sec)
	 * @return
	 */
	public float getDuration() {
		return getTime(frameCount + 1) / 1000.0f - getTime();
	}
	@Override
	public void analyze(IFileReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
	}
	/**
	 * パケットから現在時刻を取得する。
	 * @return
	 */
	protected int getTime(int frameCount) {
		if(layer == 0) { // layer1
			return (int)Math.floor(frameCount * 384 / samplingRate);
		}
		else if(layer == 1) { // layer2
			return (int)Math.floor(frameCount * 1152 / samplingRate);
		}
		else if(layer == 2) { // layer3
			if(mpegVersion == 0) {
				return (int)Math.floor(frameCount * 1152 / samplingRate);
			}
			else {
				return (int)Math.floor(frameCount * 576 / samplingRate);
			}
		}
		return -1;
	}
}
