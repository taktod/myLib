/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp8;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.vp8.type.KeyFrame;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit19;
import com.ttProject.unit.extra.bit.Bit3;

/**
 * base for vp8 frame
 * @author taktod
 */
public abstract class Vp8Frame extends VideoFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Vp8Frame.class);
	private final Bit1  frameType; // 0:keyFrame
	private final Bit3  version;
	private final Bit1  showFrame; // flag for invisible?
	private final Bit19 firstPartSize;
	/** keyFrame object for ref */
	private KeyFrame keyFrame = null;
	/**
	 * constructor
	 * @param frameType
	 * @param version
	 * @param showFrame
	 * @param firstPartSize
	 */
	public Vp8Frame(Bit1 frameType, Bit3 version, Bit1 showFrame, Bit19 firstPartSize) {
		this.frameType     = frameType;
		this.version       = version;
		this.showFrame     = showFrame;
		this.firstPartSize = firstPartSize;
	}
	/**
	 * set the keyFrame
	 * @param keyFrame
	 */
	public void setKeyFrame(KeyFrame keyFrame) {
		this.keyFrame = keyFrame;
		super.setWidth(keyFrame.getWidth());
		super.setHeight(keyFrame.getHeight());
	}
	/**
	 * ref the keyFrame.
	 * @return
	 */
	protected KeyFrame getKeyFrame() {
		return keyFrame;
	}
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		connector.setLittleEndianFlg(true);
		return connector.connect(frameType, version, showFrame, firstPartSize);
	}
	/**
	 * ref the invisible?
	 * @return
	 */
	public boolean isInvisible() {
		return showFrame.get() != 1;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.VP8;
	}
}
