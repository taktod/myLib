/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;

public abstract class TheoraFrame extends AudioFrame {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.THEORA;
	}
}
