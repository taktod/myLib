/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmimawav;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;

/**
 * adpcmImaWavFrame
 * @author taktod
 */
public abstract class AdpcmImaWavFrame extends AudioFrame {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.ADPCM_IMA_WAV;
	}
}
