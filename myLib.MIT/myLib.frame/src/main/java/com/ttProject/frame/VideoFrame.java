/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

/**
 * base of videoFrame.
 * @author taktod
 */
public abstract class VideoFrame extends Frame implements IVideoFrame {
	/** dts */
	private long dts = 0;
	/** width */
	private int width;
	/** height */
	private int height;
	/** duration */
	private float duration;
	/** keyFrame flag */
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
	 * set the dts.
	 * @param dts
	 */
	public void setDts(long dts) {
		this.dts = dts;
	}
	/**
	 * set the width
	 * @param width
	 */
	protected void setWidth(int width) {
		this.width = width;
	}
	/**
	 * set the height
	 * @param height
	 */
	protected void setHeight(int height) {
		this.height = height;
	}
	/**
	 * set the duration
	 * @param duration
	 */
	public void setDuration(float duration) {
		this.duration = duration;
	}
	/**
	 * set the keyFrame flg
	 * @param keyFrame
	 */
	protected void setKeyFrame(boolean keyFrame) {
		isKeyFrame = keyFrame;
	}
}
