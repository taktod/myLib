package com.ttProject.chunk.mpegts;

import com.ttProject.chunk.MediaChunk;

/**
 * mpegtsのchunkについて保持するクラス
 * @author taktod
 */
public class MpegtsChunk extends MediaChunk {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("mpegtsChunk");
		data.append(" header:").append(isHeader());
		data.append(" timestamp:").append(getTimestamp() / 90000D);
		data.append(" duration:").append(getDuration());
		data.append(" size:").append(getBufferSize());
		return data.toString();
	}
}
