/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp9;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.vp9.type.KeyFrame;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;

/**
 * base for vp9 frame.
 * @author taktod
 */
public abstract class Vp9Frame extends VideoFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Vp9Frame.class);
	private final Bit2 frameMarker;
	private final Bit1 profile;
	private final Bit1 reserved;
	private final Bit1 refFlag;
	private final Bit1 keyFrameFlag; // flip?
	private final Bit1 invisibleFlag; // flip?
	private final Bit1 errorRes;
	/** keyFrame object for ref */
	private KeyFrame keyFrame = null;
	/**
	 * constructor
	 * @param frameMarker
	 * @param profile
	 * @param reserved
	 * @param refFlag
	 * @param keyFrameFlag
	 * @param invisibleFlag
	 * @param errorRes
	 */
	public Vp9Frame(Bit2 frameMarker, Bit1 profile, Bit1 reserved, Bit1 refFlag,
			Bit1 keyFrameFlag, Bit1 invisibleFlag, Bit1 errorRes) {
		this.frameMarker = frameMarker;
		this.profile = profile;
		this.reserved = reserved;
		this.refFlag = refFlag;
		this.keyFrameFlag = keyFrameFlag;
		this.invisibleFlag = invisibleFlag;
		this.errorRes = errorRes;
	}
	public void setKeyFrame(KeyFrame keyFrame) {
		this.keyFrame = keyFrame;
	}
	protected KeyFrame getKeyFrame() {
		return keyFrame;
	}
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(
				frameMarker, profile, reserved, refFlag, keyFrameFlag, invisibleFlag, errorRes
		);
	}
	/**
	 * is invisible?
	 * @return
	 */
	public boolean isInvisible() {
		return invisibleFlag.get() == 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.VP9;
	}
}
