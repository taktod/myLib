/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.ttProject.rtmp.header.IRtmpHeader;

/**
 * Message
 * @author taktod
 * tmp message for RtmpDecoder
 */
public class Message {
	private final IRtmpHeader header;
	private final IRtmpHeader prevHeader;
	private final ByteBuf body;
	/**
	 * constructor
	 * @param header
	 * @param prevMessage
	 */
	public Message(IRtmpHeader header, Message prevMessage) {
		this.header = header;
		if(prevMessage != null) {
			this.prevHeader = prevMessage.getHeader();
		}
		else {
			this.prevHeader = null;
		}
		body = Unpooled.buffer(header.getSize());
	}
	public IRtmpHeader getHeader() {
		return header;
	}
	public IRtmpHeader getPrevHeader() {
		return prevHeader;
	}
	public ByteBuf getBody() {
		return body;
	}
	public boolean isComplete() {
		return !body.isWritable();
	}
}
