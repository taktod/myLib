/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.theora.TheoraFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * packetType 0x81
 * string: 6byte theora
 * size(4byte) + data(string)?
 * extraCount(4byte)?
 * extraSize + data repeat this...
 * littleEndian?
 * @author taktod
 */
public class CommentHeaderFrame extends TheoraFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(CommentHeaderFrame.class);
	private Bit8 packetType = new Bit8();
	private String theoraString = "theora";
	private Bit32 venderLength = new Bit32();
	private String venderString = null;
	private Bit32 iterateNum = new Bit32();
	/**
	 * constructor
	 * @param packetType
	 * @throws Exception
	 */
	public CommentHeaderFrame(byte packetType) throws Exception {
		if(packetType != (byte)0x81) {
			throw new Exception("unexpected packet type value.");
		}
		this.packetType.set(0x81);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		String strBuffer = new String(BufferUtil.safeRead(channel, 6).array());
		if(!strBuffer.equals(theoraString)) {
			throw new Exception("theora header string is different.");
		}
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(venderLength);
		venderString = new String(BufferUtil.safeRead(channel, venderLength.get()).array());
		loader.load(iterateNum);
		if(iterateNum.get() != 0) {
			throw new Exception("header frame with meta information is unknown, I need some sample.");
		}
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		;
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		connector.setLittleEndianFlg(true);
		ByteBuffer buffer = BufferUtil.connect(
				connector.connect(packetType),
				ByteBuffer.wrap(theoraString.getBytes()),
				connector.connect(venderLength),
				ByteBuffer.wrap(venderString.getBytes()),
				connector.connect(iterateNum)
		);
		setData(buffer);
	}
}
