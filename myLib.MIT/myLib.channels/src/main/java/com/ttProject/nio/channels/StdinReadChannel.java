/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


/**
 * 標準入力のデータを扱うreadChannel
 * 実装の仕方が非常に気に入らないけど、とりあえず仕方ない
 * @author taktod
 */
public class StdinReadChannel implements IReadChannel {
	/** 扱うreadChannel */
	private final ReadableByteChannel channel;
	/** 位置情報 */
	private int pos;
	/**
	 * コンストラクタ
	 */
	public StdinReadChannel() {
		channel = Channels.newChannel(System.in);
		pos = 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		// 閉じるという概念はない
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOpen() {
		// 閉じているという概念はない
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int position() throws IOException {
		return pos;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IReadChannel position(int newPosition) throws IOException {
		// 位置変更はありませんが、先に進むのは許可します。
		if(newPosition > pos) {
			try {
				ByteBuffer buf = ByteBuffer.allocate(newPosition - pos);
				// データを読み捨てる動作が必要
				while(newPosition > pos) {
					read(buf);
					Thread.sleep(10);
				}
			}
			catch(Exception e) {
			}
		}
		else if(newPosition != pos) {
			throw new RuntimeException("cannot rewind.");
		}
		return this;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(ByteBuffer dst) throws IOException {
		int startPos = dst.position();
		channel.read(dst);
		this.pos += dst.position() - startPos;
		return dst.position() - startPos;
	}
	/**
	 * {@inheritDoc}
	 * 数値の最大値まで読み込めるものとします
	 */
	@Override
	public int size() throws IOException {
		return Integer.MAX_VALUE;
	}
}
