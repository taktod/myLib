/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mjpeg;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoFrame;

/**
 * mjpeg frame.
 * (jpeg)
 * @author taktod
 */
public abstract class MjpegFrame extends VideoFrame {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.MJPEG;
	}
}
