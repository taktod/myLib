/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * make IReadChannel from ReadableByteChannel object.
 * no rewind. size is taken as infinite.
 * @author taktod
 */
public class ReadableByteReadChannel implements IReadChannel {
	/** target ReadableByteChannel */
	private final ReadableByteChannel channel;
	/** position */
	private int pos;
	/**
	 * constructor
	 * @param channel
	 */
	public ReadableByteReadChannel(ReadableByteChannel channel) {
		this.channel = channel;
		pos = 0;
	}
	/**
	 * default constructor
	 * for stdin
	 */
	public ReadableByteReadChannel() {
		this(Channels.newChannel(System.in));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
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
	public int position() throws IOException {
		return pos;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IReadChannel position(int newPosition) throws IOException {
		// back is not allowed, but forward is allowed.
		if(newPosition > pos) {
			try {
				ByteBuffer buf = ByteBuffer.allocate(newPosition - pos);
				// dispose data.
				while(newPosition > pos) {
					read(buf);
					Thread.sleep(10);
				}
			}
			catch(Exception e) {
			}
		}
		else if(newPosition != pos) {
			throw new RuntimeException("cannot rewind.");
		}
		return this;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(ByteBuffer dst) throws IOException {
		int startPos = dst.position();
		if(channel.read(dst) == -1) {
			throw new IOException("eof");
		}
		this.pos += dst.position() - startPos;
		return dst.position() - startPos;
	}
	/**
	 * {@inheritDoc}
	 * treat as maximum of integer.
	 */
	@Override
	public int size() throws IOException {
		return Integer.MAX_VALUE;
	}
}
