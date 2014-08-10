/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.opus;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.opus.type.HeaderFrame;

/**
 * opusのframe
 * @author taktod
 */
public abstract class OpusFrame extends AudioFrame {
	private HeaderFrame headerFrame = null;
	public void setHeaderFrame(HeaderFrame headerFrame) {
		this.headerFrame = headerFrame;
	}
	protected HeaderFrame getHeaderFrame() {
		return headerFrame;
	}
	public abstract boolean isComplete();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.OPUS;
	}
}
