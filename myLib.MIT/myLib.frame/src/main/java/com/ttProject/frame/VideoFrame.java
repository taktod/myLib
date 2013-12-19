package com.ttProject.frame;

/**
 * ビデオフレームデータ
 * @author taktod
 */
public abstract class VideoFrame extends Frame implements IVideoFrame {
	/** dts値 */
	private long dts;
	/** 横幅 */
	private int width;
	/** 縦幅 */
	private int height;
	/** データ間隔(予想) */
	private float duration;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getDts() {
		return dts;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getWidth() {
		return width;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return height;
	}
	@Override
	public float getDuration() {
		return duration;
	}
	protected void setDts(long dts) {
		this.dts = dts;
	}
	protected void setWidth(int width) {
		this.width = width;
	}
	protected void setHeight(int height) {
		this.height = height;
	}
	public void setDuration(float duration) {
		this.duration = duration;
	}
}
