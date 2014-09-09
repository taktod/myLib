/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.h264.frame;

import com.ttProject.media.h264.Frame;

/**
 * PictureParameterSet
 * mediaSequenceHeaderをつくるのに必要
 * @author taktod
 */
public class PictureParameterSet extends Frame {
	public PictureParameterSet(int size, byte frameTypeData) {
		super(size, frameTypeData);
	}
	public PictureParameterSet(byte frameTypeData) {
		this(0, frameTypeData);
	}
}
