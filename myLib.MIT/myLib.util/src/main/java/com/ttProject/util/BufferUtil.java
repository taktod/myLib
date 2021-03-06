/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;

/**
 * util for byteBuffer
 * @author taktod
 */
public class BufferUtil {
	public static ByteBuffer safeRead(ReadableByteChannel ch, int length) throws Exception {
		return safeRead(ch, length, -1);
	}
	public static ByteBuffer safeRead(ReadableByteChannel ch, int length, int timeout) throws Exception {
		return safeRead(ch, length, timeout, -1);
	}
	public static ByteBuffer safeRead(ReadableByteChannel ch, int length, int timeout, int tryCount) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(length);
		int count = 0;
		while(true) {
			ch.read(buffer);
			if(buffer.limit() == length) {
				buffer.flip();
				return buffer;
			}
			if(tryCount != -1 && tryCount < count) {
				throw new Exception("exceed the count of retry.");
			}
			if(timeout != -1 && timeout < count * 10) {
				throw new InterruptedException("buffer read time out.");
			}
			Thread.sleep(10);
			count ++;
		}
	}
	public static ByteBuffer safeRead(IReadChannel ch, int length) throws Exception {
		return safeRead(ch, length, -1);
	}
	public static ByteBuffer safeRead(IReadChannel ch, int length, int timeout) throws Exception {
		return safeRead(ch, length, timeout, -1);
	}
	public static ByteBuffer safeRead(IReadChannel ch, int length, int timeout, int tryCount) throws Exception {
		if(ch.size() - ch.position() < length) {
			throw new Exception("try to read larger data.");
		}
		ByteBuffer buffer = ByteBuffer.allocate(length);
		int count = 0;
		while(true) {
			ch.read(buffer);
			if(buffer.position() == length) {
				buffer.flip();
				return buffer;
			}
			if(tryCount != -1 && tryCount < count) {
				throw new Exception("exceed the count of retry.");
			}
			if(timeout != -1 && timeout < count * 10) {
				throw new InterruptedException("buffer read time out.");
			}
			Thread.sleep(10);
			count ++;
		}
	}
	/**
	 * copy from source to target with the size.
	 * @param source
	 * @param target
	 * @param size
	 * @throws Exception
	 */
	public static void quickCopy(IReadChannel source, WritableByteChannel target, int size) throws Exception {
		ByteBuffer buffer = null;
		int targetSize = size;
		while(targetSize > 0) {
			int bufSize = (16777216 > targetSize) ? targetSize : 16777216;
			buffer = ByteBuffer.allocate(bufSize);
			source.read(buffer);
			buffer.flip();
			if(buffer.remaining() == 0) {
				// TODO this could be exception, because the reading is on the middle.
				break;
			}
			Thread.sleep(10);
			targetSize -= buffer.remaining();
			target.write(buffer);
		}
	}
	/**
	 * dispose the size of bytes
	 * @param source
	 * @param size
	 * @throws Exception
	 */
	public static void quickDispose(IReadChannel source, int size) throws Exception {
		ByteBuffer buffer = null;
		int targetSize = size;
		while(targetSize > 0) {
			int bufSize = (16777216 > targetSize) ? targetSize : 16777216;
			buffer = ByteBuffer.allocate(bufSize);
			source.read(buffer);
			buffer.flip();
			if(buffer.remaining() == 0) {
				// TODO this could be exception, because the reading is on the middle.
				break;
			}
			Thread.sleep(10);
			targetSize -= buffer.remaining();
		}
	}
	/**
	 * get tag string from 4bytes buffer.(with lower case)
	 * for mp4 reading
	 * @param buffer
	 * @return
	 */
	public static String getDwordText(ByteBuffer buffer) {
		byte[] data = new byte[4];
		buffer.get(data);
		return new String(data).toLowerCase();
	}
	/**
	 * get tag string from 4bytes buffer.
	 * for mp4 reading
	 * @param buffer
	 * @return
	 */
	public static String getDwordTextNormal(ByteBuffer buffer) {
		byte[] data = new byte[4];
		buffer.get(data);
		return new String(data);
	}
	/**
	 * write int
	 * @param target
	 * @param data
	 * @throws IOException
	 */
	public static void writeInt(WritableByteChannel target, int data) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(data);
		buffer.flip();
		target.write(buffer);
	}
	/**
	 * check if the buffer is same.
	 * (maybe hashCode compalision is enough, this func is no needed)
	 * @param src
	 * @param dst
	 * @return true:same false:different
	 */
	public static boolean isSame(ByteBuffer src, ByteBuffer dst) {
		if(src.remaining() != dst.remaining()) {
			return false;
		}
		while(src.remaining() > 0 && src.get() == dst.get()) {
			;
		}
		return src.remaining() == 0;
	}
	/**
	 * make byte array from byteBuffer. 
	 * @param src
	 * @return
	 */
	public static byte[] toByteArray(ByteBuffer src) {
		int size = src.remaining();
		byte[] data = new byte[size];
		src.get(data);
		return data;
	}
	/**
	 * connect byteBuffers.
	 * @param buffers
	 * @return
	 */
	public static ByteBuffer connect(ByteBuffer ... buffers) {
		int length = 0;
		for(ByteBuffer buf : buffers) {
			if(buf != null) {
				length += buf.remaining();
			}
		}
		ByteBuffer result = ByteBuffer.allocate(length);
		for(ByteBuffer buf : buffers) {
			if(buf != null) {
				result.put(buf);
			}
		}
		result.flip();
		return result;
	}
	/**
	 * connect byteBuffers.
	 * @param buffers
	 * @return
	 */
	public static ByteBuffer connect(List<ByteBuffer> buffers) {
		int length = 0;
		for(ByteBuffer buf : buffers) {
			if(buf != null) {
				length += buf.remaining();
			}
		}
		ByteBuffer result = ByteBuffer.allocate(length);
		for(ByteBuffer buf : buffers) {
			if(buf != null) {
				result.put(buf);
			}
		}
		result.flip();
		return result;
	}
}
