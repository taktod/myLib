/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.nio;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;

/**
 * buffer for access data.
 * @author taktod
 */
public class CacheBuffer {
	/** buffer */
	private ByteBuffer buffer = null;
	/** target channel */
	private IReadChannel targetChannel;
	/** position for process */
	private int position;
	/** remain data for channel */
	private int remaining;
	/**
	 * constructor
	 * @param source
	 * @throws Exception
	 */
	public CacheBuffer(IReadChannel source) throws Exception {
		this(source, source.size() - source.position());
	}
	/**
	 * constructor
	 * @param source
	 * @param size
	 * @throws Exception
	 */
	public CacheBuffer(IReadChannel source, int size) throws Exception {
		this.targetChannel = source;
		this.position = source.position();
		this.remaining = size;
	}
	/**
	 * get byte
	 * @return
	 * @throws Exception
	 */
	public byte get() throws Exception {
		resetData(1);
		return buffer.get();
	}
	/**
	 * get short
	 * @return
	 * @throws Exception
	 */
	public short getShort() throws Exception {
		resetData(2);
		return buffer.getShort();
	}
	/**
	 * get long
	 * @return
	 * @throws Exception
	 */
	public long getLong() throws Exception {
		resetData(8);
		return buffer.getLong();
	}
	/**
	 * get int
	 * @return
	 * @throws Exception
	 */
	public int getInt() throws Exception {
		resetData(4);
		return buffer.getInt();
	}
	/**
	 * get midium (read 3byte integer)
	 * @return
	 * @throws Exception
	 */
	public int getMidiumInt() throws Exception {
		resetData(3);
		return (buffer.get() << 16) + buffer.getShort();
	}
	/**
	 * get buffer data for anysize.
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer getBuffer(int size) throws Exception {
		resetData(size);
		byte[] data = new byte[size];
		buffer.get(data);
		return ByteBuffer.wrap(data);
	}
	/**
	 * if cache is short, get more cache.
	 * @throws Exception
	 */
	private void resetData(int bytesLoad) throws Exception {
		// check the cache size
		if(buffer == null || buffer.remaining() < bytesLoad) {
			// check the remain data size
			if(remaining == 0 && buffer.remaining() == 0) {
				throw new Exception("eof already");
			}
			int bufRemain;
			if(buffer == null) {
				bufRemain = 0;
			}
			else {
				bufRemain = buffer.remaining();
			}
			// load data
			int bufSize = (16777216 > remaining) ? remaining : 16777216;
			ByteBuffer buf = ByteBuffer.allocate(bufSize);
			targetChannel.position(position);
			targetChannel.read(buf);
			buf.flip();
			// update information.
			position += buf.remaining();
			remaining -= buf.remaining();
			// update buffer data.
			ByteBuffer buf2 = ByteBuffer.allocate(buf.remaining() + bufRemain);
			if(bufRemain != 0) {
				buf2.put(buffer);
			}
			buf2.put(buf);
			buf2.flip();
			buffer = buf2;
		}
		if(buffer.remaining() < bytesLoad) {
			throw new Exception("data is too short.");
		}
	}
	/**
	 * size of remaining
	 * @return
	 */
	public int remaining() {
		if(buffer == null) {
			return remaining;
		}
		return remaining + buffer.remaining();
	}
	/**
	 * position
	 * @return
	 */
	public int position() {
		return position - buffer.remaining();
	}
}
