/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("mp3Chunk");
		data.append(" header:").append(isHeader());
		data.append(" timestamp:").append(getTimestamp());
		data.append(" duration:").append(getDuration());
		data.append(" size:").append(getBufferSize());
		return data.toString();
	}
}
