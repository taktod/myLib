package com.ttProject.container;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;

/**
 * コンテナの基本となるクラス
 */
public abstract class Container implements IContainer {
	private int size;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getPts() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimebase() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPosition() {
		return 0;
	}
}
