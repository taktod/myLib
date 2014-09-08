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

/**
 * theoraのkeyFrame
 * @author taktod
 */
public class IntraFrame extends TheoraFrame {
	/**
	 * コンストラクタ
	 * @param packetType
	 * @throws Exception
	 */
	public IntraFrame(byte packetType) throws Exception {
		
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

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {

	}
}
