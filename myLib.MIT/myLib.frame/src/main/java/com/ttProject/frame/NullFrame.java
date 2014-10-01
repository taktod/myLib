/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;

/**
 * 空のフレーム
 * @author taktod
 */
public class NullFrame extends Frame {
	/** インスタンス */
	private static final NullFrame instance = new NullFrame();
	/**
	 * 応答として代表のインスタンスを応答します
	 */
	public static NullFrame getInstance() {
		return instance;
	}
	/**
	 * コンストラクタ(privateにして他で作成禁止)
	 */
	private NullFrame() {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		throw new RuntimeException("NullFrame doesn't support packBuffer.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDuration() {
		throw new RuntimeException("NullFrame doesn't support duration.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		throw new RuntimeException("NullFrame doesn't support minimumLoad.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		throw new RuntimeException("NullFrame doesn't support load.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		throw new RuntimeException("NullFrame doesn't support byte data response.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.NONE;
	}
}
