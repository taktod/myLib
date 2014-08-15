/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.pcmalaw.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.pcmalaw.PcmalawFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * pcm_alawのframe
 * 160 : 0.02秒 160byteになるっぽい flvの場合
 * 576 : 0.072秒 xuggleのpacketの場合
 * 1 : 1/8000秒 riffの基本unitによると・・・
 * 8000 : 1秒
 * いろいろとやってみた結果
 * pcm_alawに関してflvにいれる場合のデータ量については、特に規程なさそうです。
 * xuggleに流し込む場合も特に規程なければいいけど・・・
 * あとwavファイルの場合の最小ユニット量が1byteになっていて、そのまま扱うと粒度が小さすぎて無駄なので、なんとかしておきたいところ
 * @author taktod
 */
public class Frame extends PcmalawFrame {
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
