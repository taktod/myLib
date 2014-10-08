/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

import java.nio.ByteBuffer;

import com.ttProject.unit.Unit;

/**
 * base for frame
 * @author taktod
 */
public abstract class Frame extends Unit implements IFrame {
	/** reading position(no necessary?) */
	private int readPosition = 0;
	/**
	 * set read position
	 * @param position
	 */
	protected void setReadPosition(int position) {
		this.readPosition = position;
	}
	/**
	 * ref read position
	 * @return
	 */
	protected int getReadPosition() {
		return readPosition;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPts(long pts) {
		super.setPts(pts);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimebase(long timebase) {
		super.setTimebase(timebase);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPrivateData() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(0);
		buffer.flip();
		return buffer;
	}
}
