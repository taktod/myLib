/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.pcmmulaw;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;

/**
 * pcm_mulaw flashでいうところのG711Aにあたります。
 * @author taktod
 */
public abstract class PcmmulawFrame extends AudioFrame {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.PCM_MULAW;
	}
}
