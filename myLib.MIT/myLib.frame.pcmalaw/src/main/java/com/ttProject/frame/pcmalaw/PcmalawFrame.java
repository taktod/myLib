/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.pcmalaw;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;

/**
 * pcm_alaw flashでいうところのG711Aにあたります。
 * @author taktod
 */
public abstract class PcmalawFrame extends AudioFrame {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.PCM_ALAW;
	}
}
