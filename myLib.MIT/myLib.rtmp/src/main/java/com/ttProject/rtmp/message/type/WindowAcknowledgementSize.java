/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message.type;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.MessageType;
import com.ttProject.rtmp.message.RtmpMessage;

/**
 * WindowAcknowledgementSize
 * @author taktod
 */
public class WindowAcknowledgementSize extends RtmpMessage {
	private int acknowledgementSize;
	/**
	 * constructor
	 */
	public WindowAcknowledgementSize() {
		super();
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public WindowAcknowledgementSize(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageType getMessageType() {
		return MessageType.WINDOW_ACKNOWLEDGEMENT_SIZE;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(acknowledgementSize);
		buffer.flip();
		return buffer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(ByteBuf in) throws Exception {
		acknowledgementSize = in.readInt();
	}
	public int getAcknowledgementSize() {
		return acknowledgementSize;
	}
	public void setAcknowledgementSize(int acknowledgementSize) {
		this.acknowledgementSize = acknowledgementSize;
	}
}
