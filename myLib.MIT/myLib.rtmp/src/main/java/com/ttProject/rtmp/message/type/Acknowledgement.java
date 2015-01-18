/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message.type;

import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;

import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.MessageType;
import com.ttProject.rtmp.message.RtmpMessage;

public class Acknowledgement extends RtmpMessage {
	private int size;
	public Acknowledgement() {
		super();
	}
	public Acknowledgement(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	@Override
	protected MessageType getMessageType() {
		return MessageType.ACKNOWLEDGEMENT;
	}
	@Override
	public ByteBuffer getData() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(size);
		buffer.flip();
		return buffer;
	}
	@Override
	public void setData(ByteBuf in) throws Exception {
		size = in.readInt();
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}
