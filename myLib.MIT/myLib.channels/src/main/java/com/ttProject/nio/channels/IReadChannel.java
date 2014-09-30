/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * read channel
 * @author taktod
 */
public interface IReadChannel extends ReadableByteChannel {
	/**
	 * check opened.
	 * @return true:opened false:not opened
	 */
	@Override
	public boolean isOpen();
	/**
	 * get size
	 * @return size
	 * @throws IOException
	 */
	public int size() throws IOException;
	/**
	 * get current position
	 * @return current cursor position
	 * @throws IOException
	 */
	public int position() throws IOException;
	/**
	 * change current position
	 * @param newPosition 
	 * @return read channel object
	 * @throws IOException
	 */
	public IReadChannel position(int newPosition) throws IOException;
	/**
	 * read(read channel can be response shorter than expected.)
	 * @param dst buffer for store.
	 * @return the read size.
	 * @throws IOException
	 */
	@Override
	public int read(ByteBuffer dst) throws IOException;
	/**
	 * close
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException;
}
