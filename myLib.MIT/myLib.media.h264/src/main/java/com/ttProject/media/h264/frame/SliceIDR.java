/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.h264.frame;

import com.ttProject.media.h264.Frame;

/**
 * sliceIDR(keyFrame)
 * @author taktod
 *
 */
public class SliceIDR extends Frame {
	public SliceIDR(int size, byte frameTypeData) {
		super(size, frameTypeData);
	}
	public SliceIDR(byte frameTypeData) {
		this(0, frameTypeData);
	}
}
