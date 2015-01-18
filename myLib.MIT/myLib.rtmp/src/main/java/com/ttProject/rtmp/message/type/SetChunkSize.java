/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message.type;

import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.MessageType;
import com.ttProject.rtmp.message.RtmpMessage;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit31;

/**
 * SetChunkSize
 * @author taktod
 */
public class SetChunkSize extends RtmpMessage {
	private Bit1  zeroBit;
	private Bit31 chunkSize;
	/**
	 * constructor
	 */
	public SetChunkSize() {
		super();
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public SetChunkSize(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageType getMessageType() {
		return MessageType.SET_CHUNK_SIZE;
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
	public void setData(ByteBuf in) throws Exception {
		zeroBit = new Bit1();
		chunkSize = new Bit31();
		IReadChannel channel = new ByteReadChannel(in.nioBuffer());
		BitLoader loader = new BitLoader(channel);
		loader.load(zeroBit, chunkSize);
	}
	public int getChunkSize() {
		return chunkSize.get();
	}
}
