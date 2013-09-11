package com.ttProject.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import com.ttProject.nio.channels.IReadChannel;

/**
 * buffer用の便利関数
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
	public static void quickCopy(IReadChannel source, WritableByteChannel target, int size) throws Exception {
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
			Thread.sleep(10);
			targetSize -= buffer.remaining();
			target.write(buffer);
		}
	}
	/**
	 * 指定したサイズを速やかに捨てる
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
				// ここで抜ける場合は例外にしておいた方が本当はよさそう。(中途で読み込みが完全にできなくなった場合になるため。)
				break;
			}
			Thread.sleep(10);
			targetSize -= buffer.remaining();
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
	/**
	 * 数値データを書き込む
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
	 * ２つのbufferが一致するか確認する。
	 * @param src
	 * @param dst
	 * @return true:一致する false:一致しない
	 */
	public static boolean isSame(ByteBuffer src, ByteBuffer dst) {
		// 長さが一致しなければ一致しない。
		if(src.remaining() != dst.remaining()) {
			return false;
		}
		// 一致したらループをまわしておく。
		while(src.remaining() > 0 && src.get() == dst.get()) {
			;
		}
		return src.remaining() == 0; // 最後まで読み込みできたなら一致
	}
}
