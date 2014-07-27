/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmimawav.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 16bit 初期振幅
 * 8bit 初期index
 * 8bit reservedBit(0x00が普通はいっている)
 * 16bit 初期振幅(right)
 * 8bit 初期index(right)
 * 8bit reservedBit(0x00が普通はいっている)
 * その後は4byte left 4byte right 4byte left....という形になっている。
 * モノラルの場合はrightの部分が抜け落ちる
 * 
 * @author taktod
 */
public class Frame extends AdpcmImaWavFrame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	/** 内包バッファ */
	private ByteBuffer buffer = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setSize(channel.size());
		// timebaseは周波数にあわせておく。
		super.setTimebase(getSampleRate());
		// sampleNumはbyteサイズから取得しておく。
		switch(getChannel()) {
		case 1: // モノラルの場合
			// 始めの4byte以外のデータがサンプル数となります。 + 1は始めの振幅分
			super.setSampleNum((channel.size() - 4) * 2 + 1);
			break;
		case 2: // ステレオの場合
			// 始めの8byte以外のデータがサンプル数となります。 + 1は始めの振幅分
			super.setSampleNum((channel.size() - 8) + 1);
			break;
		default:
			throw new RuntimeException("ステレオモノラル以外の音声データは不明です。");
		}
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, channel.size());
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		setData(buffer);
	}
}
