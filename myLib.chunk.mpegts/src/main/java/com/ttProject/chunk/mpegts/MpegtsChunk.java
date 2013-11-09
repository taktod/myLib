package com.ttProject.chunk.mpegts;

import java.nio.ByteBuffer;

import com.ttProject.chunk.MediaChunk;

/**
 * mpegtsのchunkについて保持するクラス
 * @author taktod
 *
 */
public class MpegtsChunk extends MediaChunk {
	@Override
	public boolean isHeader() {
		return false;
	}
	@Override
	public boolean write(ByteBuffer data) {
		return false;
	}
	@Override
	public float getDuration() {
		return 0;
	}
	@Override
	public byte[] getRawData() {
		return null;
	}
}
