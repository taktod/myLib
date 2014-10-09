/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora;

import java.nio.ByteBuffer;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.theora.type.IdentificationHeaderDecodeFrame;

/**
 * base of theora frame
 * @author taktod
 */
public abstract class TheoraFrame extends VideoFrame {
	/** identificationHeaderDecodeFrame for ref */
	private IdentificationHeaderDecodeFrame identificationHeaderDecodeFrame = null;
	/**
	 * add header information.
	 * @param frame
	 */
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPrivateData() throws Exception {
		if(identificationHeaderDecodeFrame != null) {
			return identificationHeaderDecodeFrame.getPrivateData();
		}
		return super.getPrivateData();
	}
}
