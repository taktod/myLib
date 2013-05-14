package com.ttProject.media.flv;

import java.nio.channels.WritableByteChannel;

import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvデータのタグ
 * @author taktod
 */
public abstract class Tag {
	private final int size;
	private final int position;
	private int timestamp;
	public Tag(final int size, final int position, final int timestamp) {
		this.size = size;
		this.position = position;
		this.timestamp = timestamp;
	}
	// そのままコピーする動作も必要
	public void copy(IFileReadChannel ch, WritableByteChannel target) throws Exception {
		int position = ch.position();
		// 開始位置をいれておく
		ch.position(getPosition());
		BufferUtil.quickCopy(ch, target, getSize() + 11 + 4);
		ch.position(position);
	}
	public abstract void analyze(IFileReadChannel ch) throws Exception;
	public int getSize() {
		return size;
	}
	public int getPosition() {
		return position;
	}
	public int getTimestamp() {
		return timestamp;
	}
}
