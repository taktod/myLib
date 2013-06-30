package com.ttProject.media.mp3.frame;

import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mp3のベースフレーム
 * @author taktod
 * 良く考えたらframeSizeは、header情報から生成する感じだったはず・・・うーん
 */
public class Mp3Frame extends Frame {
	private final byte mpegVersion; // 2:mpeg2.5(unofficial) 2:mpeg2 3:mpeg1
	private final byte layer; // 2:layer3 1:layer2 0:layer1
	private final int bitrate;// kbps
	private final float samplingRate; // kHz
	private final byte paddingBit;
	private final byte channelMode; // 0:stereo 1:joint stereo 2:Dual channel(2mono channels) 3:monoral
	private final int frameCount;
	public Mp3Frame(int position, int size,
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
	 * このフレームの始まる時間情報
	 * @return
	 */
	public float getTime() {
		return Frame.getTime(mpegVersion, layer, frameCount, samplingRate) / 1000.0f;
	}
	/**
	 * このフレームの時間情報
	 * @return
	 */
	public float getDuration() {
		return Frame.getTime(mpegVersion, layer, frameCount + 1, samplingRate) / 1000.0f - getTime();
	}
	@Override
	public void analyze(IFileReadChannel ch, IFrameAnalyzer analyzer)
			throws Exception {
	}
}
