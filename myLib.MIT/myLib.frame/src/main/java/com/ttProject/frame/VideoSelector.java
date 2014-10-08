/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

import com.ttProject.unit.ISelector;

/**
 * base selector for videoFrame.
 * @author taktod
 */
public abstract class VideoSelector implements ISelector {
	// default value is settled by container.
	/** width */
	private int width;
	/** height */
	private int height;
	/**
	 * set the width
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * set the height.
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * setup container info on the frame.
	 * @param frame
	 */
	public void setup(VideoFrame frame) {
		frame.setWidth(width);
		frame.setHeight(height);
	}
}
