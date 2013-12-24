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
	/** キーフレームであるかどうか */
	private boolean isKeyFrame = false;
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDuration() {
		return duration;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isKeyFrame() {
		return isKeyFrame;
	}
	/**
	 * dts値設定
	 * @param dts
	 */
	public void setDts(long dts) {
		this.dts = dts;
	}
	/**
	 * 横幅設定
	 * @param width
	 */
	protected void setWidth(int width) {
		this.width = width;
	}
	/**
	 * 縦幅設定
	 * @param height
	 */
	protected void setHeight(int height) {
		this.height = height;
	}
	/**
	 * duration値設定
	 * @param duration
	 */
	public void setDuration(float duration) {
		this.duration = duration;
	}
	/**
	 * keyFrameであるか設定
	 * @param keyFrame
	 */
	protected void setKeyFrame(boolean keyFrame) {
		isKeyFrame = keyFrame;
	}
}
