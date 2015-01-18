/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message;

import io.netty.buffer.ByteBuf;

import com.ttProject.rtmp.header.HeaderFactory;
import com.ttProject.rtmp.header.IRtmpHeader;

/**
 * RtmpMessage
 * @author taktod
 */
public abstract class RtmpMessage implements IRtmpMessage {
	private final IRtmpHeader header;
	/**
	 * constructor
	 */
	public RtmpMessage() {
		header = HeaderFactory.getInstance().getHeader(getMessageType());
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public RtmpMessage(IRtmpHeader header, ByteBuf in) throws Exception {
		this.header = header;
		setData(in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IRtmpHeader getHeader() {
		return header;
	}
	/**
	 * messageType definition.
	 * @return
	 */
	protected abstract MessageType getMessageType();
}
