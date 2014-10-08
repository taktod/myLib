/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;

/**
 * mock frame for dummy.
 * @author taktod
 */
public class NullFrame extends Frame {
	/** shared instance */
	private static final NullFrame instance = new NullFrame();
	/**
	 * ref the shared instance.
	 */
	public static NullFrame getInstance() {
		return instance;
	}
	/**
	 * constructor
	 * (prohibit by private.)
	 */
	private NullFrame() {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		throw new RuntimeException("NullFrame doesn't support packBuffer.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDuration() {
		throw new RuntimeException("NullFrame doesn't support duration.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		throw new RuntimeException("NullFrame doesn't support minimumLoad.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		throw new RuntimeException("NullFrame doesn't support load.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		throw new RuntimeException("NullFrame doesn't support byte data response.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.NONE;
	}
}
