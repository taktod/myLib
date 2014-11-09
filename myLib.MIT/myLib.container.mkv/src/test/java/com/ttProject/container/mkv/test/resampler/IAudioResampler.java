package com.ttProject.container.mkv.test.resampler;

public interface IAudioResampler {
	public void setQuality(int quality);
	public void close();
	public int processFloat(long channelIndex, float[] in, long inLen, float[] out, long outLen);
	public int processInt(long channelIndex, float[] in, long inLen, float[] out, long outLen);
	public int processInterleavedFloat(float[] in, long inLen, float[] out, long outLen);
	public int processInterleavedInt(float[] in, long inLen, float[] out, long outLen);
	public boolean setRate(long inRate, long outRate);
	public long getInRate();
	public long getOutRate();
	public long getRatioNum();
	public long getRatioDen();
	public int getQuality();
	public void setInputStride(long stride);
	public long getInputStride();
	public void setOutputStride(long stride);
	public long getOutputStride();
	public long getInputLatency();
	public long getOutputLatency();
	public boolean skipZeros();
	public boolean resetMem();
}
