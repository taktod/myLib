/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * read channel for byte memory data.
 * @author taktod
 */
public class ByteReadChannel implements IReadChannel {
	/** buffer */
	private final ByteBuffer buffer;
	/**
	 * constructor(ByteBuffer)
	 * @param buffer
	 */
	public ByteReadChannel(ByteBuffer buffer) {
		this.buffer = buffer.duplicate();
	}
	/**
	 * constructor(byte[])
	 * @param data
	 */
	public ByteReadChannel(byte[] data) {
		buffer = ByteBuffer.allocate(data.length);
		buffer.put(data);
		buffer.flip();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOpen() {
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() throws IOException {
		return buffer.limit();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int position() throws IOException {
		return buffer.position();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IReadChannel position(int newPosition) throws IOException {
		buffer.position(newPosition);
		return this;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(ByteBuffer dst) throws IOException {
		int length = dst.limit() > buffer.remaining() ? buffer.remaining() : dst.limit();
		byte[] data = new byte[length];
		buffer.get(data);
		dst.put(data);
		return length;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
	}
}
