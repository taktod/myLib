/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp6;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.vp6.type.IntraFrame;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * vp6 frame.
 * on2Vp6
 * @see http://wiki.multimedia.cx/index.php?title=On2_VP6
 * 00 78 46 0F 14 0F 14 3F 6E E8 CB 01 8D C9 89 26 9E AD 53 6F 33 FD DD F2 BF AB F6 ED FB 1C
 * 
 * for flv data, first data of vp6 need to be the end of data.
 * so data start with 0x78
 * 
 * @see http://hkpr.info/flash/swf/index.php?%E3%83%93%E3%83%87%E3%82%AA%2FOn2%20Truemotion%20VP6%20%E3%83%93%E3%83%83%E3%83%88%E3%82%B9%E3%83%88%E3%83%AA%E3%83%BC%E3%83%A0%E3%83%95%E3%82%A9%E3%83%BC%E3%83%9E%E3%83%83%E3%83%88
 * on2vp6alphaも
 * はじめの00の部分はalignのずれ設定みたいですね。
 * データは捨てた方がよさそう・・・
 * 
 * @author taktod
 */
public abstract class Vp6Frame extends VideoFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Vp6Frame.class);
	private final Bit1 frameMode;
	private final Bit6 qp;
	private final Bit1 marker;
	/** key frame object for ref */
	private IntraFrame keyFrame = null;
	/**
	 * constructor
	 * @param frameMode
	 * @param qp
	 * @param marker
	 */
	public Vp6Frame(Bit1 frameMode, Bit6 qp, Bit1 marker) {
		this.frameMode = frameMode;
		this.qp = qp;
		this.marker = marker;
	}
	/**
	 * set the keyframe.
	 * @param keyFrame
	 */
	public void setKeyFrame(IntraFrame keyFrame) {
		this.keyFrame = keyFrame;
		super.setWidth(keyFrame.getWidth());
		super.setHeight(keyFrame.getHeight());
	}
	/**
	 * ref marker
	 * @return
	 */
	protected Bit1 getMarker() {
		return marker;
	}
	/**
	 * ref keyFrame
	 * @return
	 */
	protected IntraFrame getKeyFrame() {
		return keyFrame;
	}
	/**
	 * ref the header buffer
	 * @return
	 */
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(frameMode, qp, marker);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.VP6;
	}
}
