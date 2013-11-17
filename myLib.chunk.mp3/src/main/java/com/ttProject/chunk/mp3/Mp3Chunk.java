package com.ttProject.chunk.mp3;

import com.ttProject.chunk.MediaChunk;

/**
 * mp3のchunkについて保持するクラス
 * @author taktod
 */
public class Mp3Chunk extends MediaChunk {
	/** 保持sampleRate */
	private final int sampleRate;
	/**
	 * コンストラクタ
	 * @param sampleRate
	 */
	public Mp3Chunk(int sampleRate) {
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
}
