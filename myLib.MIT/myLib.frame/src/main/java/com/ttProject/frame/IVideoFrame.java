/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

/**
 * interface of videoFrame
 * @author taktod
 */
public interface IVideoFrame extends IFrame {
	/**
	 * dts
	 * @return
	 */
	public long getDts(); // dts is decode time?
	/**
	 * width
	 * @return
	 */
	public int getWidth();
	/**
	 * height
	 * @return
	 */
	public int getHeight();
	/**
	 * is keyFrame?
	 * @return
	 */
	public boolean isKeyFrame();
}
