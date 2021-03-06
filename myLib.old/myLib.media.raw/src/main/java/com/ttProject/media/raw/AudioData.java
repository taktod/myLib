/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.raw;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

/**
 * ベーシックなオーディオデータ
 * @author taktod
 */
public class AudioData implements Cloneable {
	/** 保持audioFormat */
	private AudioFormat format;
	/** 保持buffer */
	private ByteBuffer buffer;
	/** timestampデータ */
	private long timestamp;
	/**
	 * コンストラクタ
	 * @param format
	 * @param buffer
	 * @param timestamp
	 */
	public AudioData(AudioFormat format, ByteBuffer buffer, long timestamp) {
		this.format = format;
		this.buffer = buffer;
		this.timestamp = timestamp;
	}
	/**
	 * コンストラクタ
	 * @param format
	 * @param buffer
	 */
	public AudioData(AudioFormat format, ByteBuffer buffer) {
		this(format, buffer, -1);
	}
	/**
	 * フォーマット情報参照
	 * @return
	 */
	public AudioFormat getFormat() {
		return format;
	}
	/**
	 * bufferデータ参照
	 * @return
	 */
	public ByteBuffer getBuffer() {
		return buffer.duplicate();
	}
	/**
	 * タイムスタンプ参照
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}
	public AudioData clone() {
		return new AudioData(format, buffer, timestamp);
	}
}
