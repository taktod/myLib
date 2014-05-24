/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit48;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * vorbisのheaderフレーム
 * packetType: 1byte 0x05 setup header
 * string: 6Byte "vorbis"
 * あとのデータはよくわからん。
 * どうやらデコードするときの変換情報が含まれているらしい。
 * 
 * @see http://www.xiph.org/vorbis/doc/Vorbis_I_spec.html#x1-620004.2.2
 * @author taktod
 */
public class SetupHeaderFrame extends VorbisFrame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SetupHeaderFrame.class);
	private Bit8  packetType  = new Bit8();
	private Bit48 string      = new Bit48();
	private ByteBuffer buffer = null; // データ本体
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
//		logger.info(channel.size());
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(packetType, string);
		if(packetType.get() != 5) {
			throw new Exception("packetTypeが不正です");
		}
		if(string.getLong() != 0x736962726F76L) {
			throw new Exception("string文字列が不正です。");
		}
		super.setSize(channel.size());
		buffer = BufferUtil.safeRead(channel, channel.size() - channel.position());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		connector.setLittleEndianFlg(true);
		setData(BufferUtil.connect(
				connector.connect(packetType, string),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
}
