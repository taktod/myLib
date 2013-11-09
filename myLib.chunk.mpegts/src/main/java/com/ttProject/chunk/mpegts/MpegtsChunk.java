package com.ttProject.chunk.mpegts;

import java.nio.ByteBuffer;

import com.ttProject.chunk.MediaChunk;

/**
 * mpegtsのchunkについて保持するクラス
 * @author taktod
 *
 */
public class MpegtsChunk extends MediaChunk {
	/**
	 * headerであるかの応答
	 */
	@Override
	public boolean isHeader() {
		return false; // false固定
	}
	/**
	 * 書き込みデータ登録
	 */
	@Override
	public boolean write(ByteBuffer data) {
		// データを登録しておく。
		ByteBuffer buffer = getBuffer(data.remaining());
		buffer.put(data);
		return true;
	}
}
