/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis;

import java.nio.ByteBuffer;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.vorbis.type.IdentificationHeaderFrame;

/**
 * vorbis frame
 * @author taktod
 * vorbis frame is similar to speex
 * header
 * comment
 * setup info
 * data.
 */
public abstract class VorbisFrame extends AudioFrame {
	/** ref object of identificationHeaderFrame */
	private IdentificationHeaderFrame identificationHeaderFrame = null;
	/**
	 * set identificationHeaderFrame
	 * @param headerFrame
	 */
	public void setIdentificationHeaderFrame(IdentificationHeaderFrame headerFrame) {
		this.identificationHeaderFrame = headerFrame;
		super.setBit(headerFrame.getBit());
		super.setChannel(headerFrame.getChannel());
		super.setSampleRate(headerFrame.getSampleRate());
		super.setSampleNum(headerFrame.getSampleNum());
	}
	/**
	 * ref identificationHeaderFrame
	 * @return
	 */
	protected IdentificationHeaderFrame getHeaderFrame() {
		return identificationHeaderFrame;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPrivateData() throws Exception {
		if(identificationHeaderFrame == null) {
			return null;
		}
		return identificationHeaderFrame.getPrivateData();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.VORBIS;
	}
}
