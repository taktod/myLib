/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mjpeg.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.mjpeg.MjpegFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * mjpegの内部フレーム
 * @author taktod
 */
public class Frame extends MjpegFrame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	private ByteBuffer buffer = null;
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// ここでデータ実体を保持しておきたい。
		buffer = BufferUtil.safeRead(channel, channel.size());
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		setData(buffer);
	}
}
