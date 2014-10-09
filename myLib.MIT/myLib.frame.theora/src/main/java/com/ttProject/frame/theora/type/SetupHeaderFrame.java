/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.theora.TheoraFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * frame private data.
 * packetType 0x82
 * string 6byte theora
 * @author taktod
 */
public class SetupHeaderFrame extends TheoraFrame {
	private Bit8 packetType = new Bit8();
	private String theoraString = "theora";
	private ByteBuffer buffer = null;
	/**
	 * constructor
	 * @param packetType
	 * @throws Exception
	 */
	public SetupHeaderFrame(byte packetType) throws Exception {
		if(packetType != (byte)0x82) {
			throw new Exception("unexpected packet type value.");
		}
		this.packetType.set(0x82);
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
			throw new Exception("theora string is corrupted.");
		}
		buffer = BufferUtil.safeRead(channel, channel.size() - channel.position());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("buffer data is undefined.");
		}
		BitConnector connector = new BitConnector();
		ByteBuffer data = BufferUtil.connect(
				connector.connect(packetType),
				ByteBuffer.wrap(theoraString.getBytes()),
				buffer
		);
		setData(data);
	}
}
