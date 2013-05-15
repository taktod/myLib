package com.ttProject.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import com.ttProject.nio.channels.IFileReadChannel;

/**
 * buffer用の便利関数
 * @author todatakahiko
 *
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
	 * 指定したサイズのデータをコピーする。
	 * @param source
	 * @param target
	 * @param size
	 * @throws Exception
	 */
	public static void quickCopy(IFileReadChannel source, WritableByteChannel target, int size) throws Exception {
		ByteBuffer buffer = null;
		int targetSize = size;
		while(targetSize > 0) {
			int bufSize = (16777216 > targetSize) ? targetSize : 16777216;
			buffer = ByteBuffer.allocate(bufSize);
			source.read(buffer);
			buffer.flip();
			if(buffer.remaining() == 0) {
				// ここで抜ける場合は例外にしておいた方が本当はよさそう。(中途で読み込みが完全にできなくなった場合になるため。)
				break;
			}
			targetSize -= buffer.remaining();
			target.write(buffer);
		}
	}
	/**
	 * read状態のbufferからタグ文字列を取得する
	 * @param buffer
	 * @return
	 */
	public static String getDwordText(ByteBuffer buffer) {
		byte[] data = new byte[4];
		buffer.get(data);
		return new String(data).toLowerCase();
	}
	public static void writeInt(WritableByteChannel target, int data) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(data);
		buffer.flip();
		target.write(buffer);
	}
}