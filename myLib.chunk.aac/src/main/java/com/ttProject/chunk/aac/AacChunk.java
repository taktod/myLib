package com.ttProject.chunk.aac;

import com.ttProject.chunk.MediaChunk;

/**
 * aacのchunkについて保持するクラス
 * @author taktod
 */
public class AacChunk extends MediaChunk {
	/** 保持sampleRate */
	private final int sampleRate;
	/**
	 * コンストラクタ
	 * @param sampleRate
	 */
	public AacChunk(int sampleRate) {
		super();
		this.sampleRate = sampleRate;
	}
	/**
	 * sampleRate参照
	 * @return
	 */
	public int getSampleRate() {
		return sampleRate;
	}
	/**
	 * データ出力
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("aacChunk");
		data.append(" header:").append(isHeader());
		data.append(" timestamp:").append(getTimestamp());
		data.append(" duration:").append(getDuration());
		data.append(" size:").append(getBufferSize());
		return data.toString();
	}
}
