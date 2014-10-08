/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

/**
 * interface for audioFrame
 * @author taktod
 */
public interface IAudioFrame extends IFrame {
	/**
	 * ref the sampleNum
	 * @return
	 */
	public int getSampleNum();
	/**
	 * ref the sampleRate
	 * @return
	 */
	public int getSampleRate();
	/**
	 * ref the unit channel num
	 * @return
	 */
	public int getChannel();
	/**
	 * ref the bitdepth
	 * @return
	 */
	public int getBit();
}
