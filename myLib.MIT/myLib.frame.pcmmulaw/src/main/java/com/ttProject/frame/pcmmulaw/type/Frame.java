/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.pcmmulaw.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.pcmmulaw.PcmmulawFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * pcm_mulawのframe
 * 160 : 0.02秒 160byteになるっぽい
 * 8000 : 1秒
 * @author taktod
 */
public class Frame extends PcmmulawFrame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	/** frameの内部データ */
	private ByteBuffer frameBuffer = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setSampleRate(8000);
		super.setSampleNum(160);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.setSize(channel.size());
		// そのままデータを保持しておいておわり。
		frameBuffer = BufferUtil.safeRead(channel, channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameBufferがnullでした。先に解析してください。");
		}
		super.setData(frameBuffer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception{
		return getData();
	}
}
