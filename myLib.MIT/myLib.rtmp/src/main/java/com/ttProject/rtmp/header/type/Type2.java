/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.header.type;

import java.nio.ByteBuffer;

import com.ttProject.rtmp.header.HeaderType;
import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.header.RtmpHeader;
import com.ttProject.util.BufferUtil;

/**
 * Type2
 * @author taktod
 */
public class Type2 extends RtmpHeader {
	/**
	 * constructor
	 */
	public Type2() {
		super(HeaderType.Type2);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() {
		ByteBuffer typeCsIdBuffer = getHeaderTypeChunkStreamIdBytes();
		boolean isExtraTime = getDeltaTime() >= IRtmpHeader.MAX_TIME;
		ByteBuffer body = null;
		ByteBuffer extraTime = null;
		body = ByteBuffer.allocate(3);
		if(isExtraTime) {
			int time = IRtmpHeader.MAX_TIME;
			body.put(new byte[]{(byte)((time >> 16) & 0xFF),
					(byte)((time >> 8) & 0xFF),
					(byte)(time & 0xFF)});
			extraTime = ByteBuffer.allocate(4);
			extraTime.putInt((int)getDeltaTime());
			extraTime.flip();
		}
		else {
			int time = (int)getDeltaTime();
			body.put(new byte[]{(byte)((time >> 16) & 0xFF),
					(byte)((time >> 8) & 0xFF),
					(byte)(time & 0xFF)});
		}
		body.flip();
		return BufferUtil.connect(typeCsIdBuffer, body, extraTime);
	}
}
