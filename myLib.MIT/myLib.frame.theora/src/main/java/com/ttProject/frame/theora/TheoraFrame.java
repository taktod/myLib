/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.theora.type.IdentificationHeaderDecodeFrame;

public abstract class TheoraFrame extends VideoFrame {
	/** データ参照用のidentificationHeaderDecodeFrame */
	private IdentificationHeaderDecodeFrame identificationHeaderDecodeFrame = null;
	public void setIdentificationHeaderDecodeFrame(IdentificationHeaderDecodeFrame frame) {
		this.identificationHeaderDecodeFrame = frame;
		super.setWidth(frame.getWidth());
		super.setHeight(frame.getHeight());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.THEORA;
	}
}
