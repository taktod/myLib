/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk;

import java.nio.ByteBuffer;

/**
 * mediaChunkの共通処理を一本化
 * @author taktod
 */
public abstract class MediaChunk implements IMediaChunk {
	/** 保持データ実体 */
	private ByteBuffer buffer = null;
	/** データの長さ */
	private float duration = 0;
	/** 動作タイムスタンプ値 */
	private long timestamp = 0;
	/**
	 * 処理buffer参照
	 * @param size
	 * @return
	 */
	protected ByteBuffer getBuffer(int size) {
		if(buffer == null) { // なかったら新規作成
			buffer = ByteBuffer.allocate(size + 65536);
		}
		if(buffer.remaining() >= size) { // 容量の残りが必要量ある場合はそのまま応答
			return buffer;
		}
		// 必要量ないので、新規にバッファを再生成
		ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + size + 65536); 
		buffer.flip();
		newBuffer.put(buffer);
		buffer = newBuffer;
		return buffer;		
	}
	/**
	 * すでに追加済みのデータ量を応答する。
	 * @return
	 */
	protected int getBufferSize() {
		if(buffer == null) {
			return 0;
		}
		return buffer.position();
	}
	/**
	 * 長さ設定
	 * @param duration
	 */
	public void setDuration(float duration) {
		this.duration = duration;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDuration() {
		return duration;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getRawBuffer() {
		ByteBuffer buffer = this.buffer.duplicate();
		buffer.flip();
		return buffer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getRawData() {
		ByteBuffer buffer = getRawBuffer();
		byte[] data = new byte[buffer.limit()];
		buffer.get(data);
		return data;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	/**
	 * timestampを設置する
	 * @param pts
	 */
	public void setTimestamp(long pts) {
		timestamp = pts;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHeader() {
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean write(ByteBuffer data) {
		// データを登録しておく。
		ByteBuffer buffer = getBuffer(data.remaining());
		buffer.put(data);
		return true;
	}
}
