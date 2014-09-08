/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.theora.TheoraFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * packetType 0x82
 * string 6byte theora
 * その他データ
 * @author taktod
 */
public class SetupHeaderFrame extends TheoraFrame {
	private Bit8 packetType = new Bit8();
	private String theoraString = "theora";
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param packetType
	 * @throws Exception
	 */
	public SetupHeaderFrame(byte packetType) throws Exception {
		if(packetType != (byte)0x82) {
			throw new Exception("packetTypeの数値が一致しません");
		}
		this.packetType.set(0x82);
	}
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
		String strBuffer = new String(BufferUtil.safeRead(channel, 6).array());
		if(!strBuffer.equals(theoraString)) {
			throw new Exception("theoraの文字列が一致しません。");
		}
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
		if(buffer == null) {
			throw new Exception("bufferデータがない");
		}
		BitConnector connector = new BitConnector();
		ByteBuffer data = BufferUtil.connect(
				connector.connect(packetType),
				ByteBuffer.wrap(theoraString.getBytes()),
				buffer
		);
		setData(data);
	}
}
