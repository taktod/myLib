package com.ttProject.nio;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IFileReadChannel;

/**
 * ファイル上のデータにより高速にアクセスするためのバッファ
 * なるべくbufferからデータを取り出すことで高速アクセスを実現してみる。
 * @author taktod
 */
public class CacheBuffer {
	private ByteBuffer buffer = null;
	private IFileReadChannel targetChannel;
	private int position;
	private final int size; // これいらないかも・・・
	private int remaining; // 残り読み込みデータ量
	public CacheBuffer(IFileReadChannel source, int size) throws Exception {
		this.targetChannel = source;
		this.position = source.position();
		this.size = size;
		this.remaining = size;
	}
	/**
	 * 整数の値のみ応答することにします。
	 * @return
	 */
	public int getInt() throws Exception {
		if(buffer == null || buffer.remaining() < 4) {
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
			// bufferが足りないので読み込む必要がある。
			int bufSize = (16777216 > remaining) ? remaining : 16777216;
			ByteBuffer buf = ByteBuffer.allocate(bufSize);
			targetChannel.position(position);
			targetChannel.read(buf);
			buf.flip();
			position += buf.remaining();
			remaining -= buf.remaining();
			ByteBuffer buf2 = ByteBuffer.allocate(buf.remaining() + bufRemain);
			if(bufRemain != 0) {
				buf2.put(buffer);
			}
			buf2.put(buf);
			buf2.flip();
			buffer = buf2;
		}
		return buffer.getInt();
	}
	/**
	 * 残りデータ量を応答する
	 * @return
	 */
	public int remaining() {
		if(buffer == null) {
			return remaining;
		}
		return remaining + buffer.remaining();
	}
	public int size() {
		return size;
	}
}
