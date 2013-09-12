package com.ttProject.media.raw;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

/**
 * ベーシックなオーディオデータ
 * @author taktod
 */
public class AudioData {
	/** 保持audioFormat */
	private AudioFormat format;
	/** 保持buffer */
	private ByteBuffer buffer;
	/**
	 * コンストラクタ
	 * @param format
	 * @param buffer
	 */
	public AudioData(AudioFormat format, ByteBuffer buffer) {
		this.format = format;
		this.buffer = buffer;
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
		return buffer;
	}
}
