/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mp3.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit7;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * id3 tag
 * @author taktod
 */
public class ID3Frame extends Mp3Frame {
	private Bit24 signature = new Bit24();
	private Bit16 version   = new Bit16();
	private Bit8  flag      = new Bit8();
	private Bit1  dummy1    = new Bit1();
	private Bit7  size1     = new Bit7();
	private Bit1  dummy2    = new Bit1();
	private Bit7  size2     = new Bit7();
	private Bit1  dummy3    = new Bit1();
	private Bit7  size3     = new Bit7();
	private Bit1  dummy4    = new Bit1();
	private Bit7  size4     = new Bit7();
	
	private ByteBuffer rawBuffer = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setReadPosition(channel.position() - 1);
		Bit16 signature = new Bit16();
		BitLoader loader = new BitLoader(channel);
		loader.load(signature, version, flag,
				dummy1, size1, dummy2, size2, dummy3, size3, dummy4, size4);
		this.signature.set('I' << 16 | signature.get());
		super.setSize(10 + (size1.get() << 21 | size2.get() << 14 | size3.get() << 7 | size4.get()));
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getReadPosition() + 10);
		rawBuffer = BufferUtil.safeRead(channel, getSize() - 10);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(rawBuffer == null) {
			throw new Exception("rawBuffer is undefined.");
		}
		BitConnector connector = new BitConnector();
		super.setData(BufferUtil.connect(
				connector.connect(signature, version, flag,
						dummy1, size1, dummy2, size2,
						dummy3, size3, dummy4, size4),
				rawBuffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
}
