package com.ttProject.library;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import com.ttProject.nio.channels.IFileReadChannel;

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
	public static ByteBuffer safeRead(IFileReadChannel ch, int length) throws Exception {
		return safeRead(ch, length, -1);
	}
	public static ByteBuffer safeRead(IFileReadChannel ch, int length, int timeout) throws Exception {
		return safeRead(ch, length, timeout, -1);
	}
	public static ByteBuffer safeRead(IFileReadChannel ch, int length, int timeout, int tryCount) throws Exception {
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
}
