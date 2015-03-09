/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp9.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.vp9.Vp9Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * keyFrame for vp9
 * @author taktod
 */
public class KeyFrame extends Vp9Frame {
	/** logger */
	private Logger logger = Logger.getLogger(KeyFrame.class);
	private byte[] startCode = {(byte)0x49, (byte)0x83, (byte)0x42};
	private Bit3 colorSpace    = new Bit3();
	private Bit1 fullrange     = new Bit1();
	private Bit16 widthMinus1  = new Bit16();
	private Bit16 heightMinus1 = new Bit16();
	private Bit extraBit = null;
	private ByteBuffer buffer = null;
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
	public KeyFrame(Bit2 frameMarker, Bit1 profile, Bit1 reserved, Bit1 refFlag,
			Bit1 keyFrameFlag, Bit1 invisibleFlag, Bit1 errorRes) {
		super(frameMarker, profile, reserved, refFlag, keyFrameFlag, invisibleFlag, errorRes);
		super.setKeyFrame(true);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// at least I want to know width, height information.
		ByteBuffer buffer = BufferUtil.safeRead(channel, 3);
		if(buffer.get() != startCode[0]
		|| buffer.get() != startCode[1]
		|| buffer.get() != startCode[2]) {
			logger.info("start code of key frame is corrupted");
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(colorSpace);
		if(colorSpace.get() == 7) { // rgb = profile 1
			throw new Exception("RGB is not supported profile0.");
		}
		loader.load(fullrange, widthMinus1, heightMinus1);
		setSize(channel.size());
		setWidth(widthMinus1.get() + 1);
		setHeight(heightMinus1.get() + 1);
		setReadPosition(channel.position());
		logger.info("width:" + (widthMinus1.get() + 1));
		logger.info("height:" + (heightMinus1.get() + 1));
		extraBit = loader.getExtraBit();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("data buffer is not loaded yet.");
		}
		BitConnector connector = new BitConnector();
		setData(BufferUtil.connect(getHeaderBuffer(),
				connector.connect(new Bit8(startCode[0]), new Bit8(startCode[1]), new Bit8(startCode[2]), colorSpace, fullrange, widthMinus1, heightMinus1, extraBit),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
}
