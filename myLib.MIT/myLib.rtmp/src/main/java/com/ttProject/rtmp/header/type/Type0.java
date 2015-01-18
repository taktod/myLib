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
 * Type0
 * @author taktod
 */
public class Type0 extends RtmpHeader {
	/**
	 * constructor
	 */
	public Type0() {
		super(HeaderType.Type0);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() {
		ByteBuffer typeCsIdBuffer = getHeaderTypeChunkStreamIdBytes();
		boolean isExtraTime = getTime() >= IRtmpHeader.MAX_TIME;
		ByteBuffer body = null;
		ByteBuffer extraTime = null;
		body = ByteBuffer.allocate(11);
		if(isExtraTime) {
			int time = IRtmpHeader.MAX_TIME;
			body.put(new byte[]{(byte)((time >> 16) & 0xFF),
					(byte)((time >> 8) & 0xFF),
					(byte)(time & 0xFF)});
			extraTime = ByteBuffer.allocate(4);
			extraTime.putInt((int)getTime());
			extraTime.flip();
		}
		else {
			int time = (int)getTime();
			body.put(new byte[]{(byte)((time >> 16) & 0xFF),
					(byte)((time >> 8) & 0xFF),
					(byte)(time & 0xFF)});
		}
		int size = getSize();
		body.put(new byte[]{
				(byte)((size >> 16) & 0xFF),
				(byte)((size >> 8) & 0xFF),
				(byte)(size & 0xFF)});
		body.put((byte)getMessageType().intValue());
		int streamId = getStreamId();
		body.put(new byte[]{
				(byte)(streamId & 0xFF),
				(byte)((streamId >> 8) & 0xFF),
				(byte)((streamId >> 16) & 0xFF),
				(byte)((streamId >> 24) & 0xFF)
		});
		body.flip();
		return BufferUtil.connect(typeCsIdBuffer, body, extraTime);
	}
}
