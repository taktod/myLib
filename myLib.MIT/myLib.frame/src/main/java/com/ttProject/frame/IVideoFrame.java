/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

/**
 * 映像フレームのインターフェイス
 * @author taktod
 */
public interface IVideoFrame extends IFrame {
	/**
	 * dts値
	 * @return
	 */
	public long getDts(); // dtsはdecodeするときの時間
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
	 * keyFrameであるか
	 * @return
	 */
	public boolean isKeyFrame();
}
