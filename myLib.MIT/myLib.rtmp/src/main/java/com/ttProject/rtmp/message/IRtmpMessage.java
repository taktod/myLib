/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import com.ttProject.rtmp.header.IRtmpHeader;

/**
 * IRtmpMessage
 * @author taktod
 */
public interface IRtmpMessage {
	public IRtmpHeader getHeader();
	public ByteBuffer getData() throws Exception;
	public void setData(ByteBuf in) throws Exception;
}
