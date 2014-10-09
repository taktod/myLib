/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp6.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.vp6.Vp6Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.util.BufferUtil;

/**
 * interFrame
 * @author taktod
 */
public class InterFrame extends Vp6Frame {
	private Bit16 offset = null;

	private ByteBuffer buffer = null;
	/**
	 * constructor
	 * @param frameMode
	 * @param qp
	 * @param marker
	 */
	public InterFrame(Bit1 frameMode, Bit6 qp, Bit1 marker) {
		super(frameMode, qp, marker);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// if the version is 0 or marker is 1, need load offset.
		if(getKeyFrame().getVersion2().get() == 0 || getMarker().get() == 1) {
			BitLoader loader = new BitLoader(channel);
			offset = new Bit16();
			loader.load(offset);
		}
		super.setReadPosition(channel.position());
		super.setSize(channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(super.getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("buffer data is unload yet.");
		}
		BitConnector connector = new BitConnector();
		setData(BufferUtil.connect(getHeaderBuffer(),
				connector.connect(offset),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
}
