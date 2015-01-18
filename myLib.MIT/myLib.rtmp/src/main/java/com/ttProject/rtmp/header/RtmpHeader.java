/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.header;

import java.nio.ByteBuffer;

import com.ttProject.rtmp.header.type.Type0;
import com.ttProject.rtmp.header.type.Type1;
import com.ttProject.rtmp.header.type.Type2;
import com.ttProject.rtmp.header.type.Type3;
import com.ttProject.rtmp.message.MessageType;

/**
 * RtmpHeader
 * @author taktod
 */
public abstract class RtmpHeader implements IRtmpHeader {
	private final HeaderType headerType;
	private int  csId;
	private int  deltaTime = 0;
	private long time = 0; // possible to exceeed max of integer. Then, use long for hold.
	private int  size = 0;
	private MessageType messageType;
	private int  streamId = 0;
	/**
	 * constructor
	 * @param type
	 */
	public RtmpHeader(HeaderType type) {
		headerType = type;
	}
	@Override
	public boolean isMedia() {
		return isVideo() || isAudio() || isAggregate();
	}
	@Override
	public boolean isMetaData() {
		MessageType type = getMessageType();
		return type == MessageType.AMF3_DATA_MESSAGE || type == MessageType.AMF0_DATA_MESSAGE;
	}
	@Override
	public boolean isAggregate() {
		return getMessageType() == MessageType.AGGREGATE_MESSAGE;
	}
	@Override
	public boolean isAudio() {
		return getMessageType() == MessageType.AUDIO_MESSAGE;
	}
	@Override
	public boolean isVideo() {
		return getMessageType() == MessageType.VIDEO_MESSAGE;
	}
	@Override
	public boolean isControl() {
		return getMessageType() == MessageType.USER_CONTROL_MESSAGE;
	}
	@Override
	public boolean isChunkSize() {
		return getMessageType() == MessageType.SET_CHUNK_SIZE;
	}
	@Override
	public void setCsId(int csId) {
		this.csId = csId;
	}
	@Override
	public int getCsId() {
		return csId;
	}
	@Override
	public long getTime() {
		return time;
	}
	@Override
	public MessageType getMessageType() {
		return messageType;
	}
	@Override
	public int getStreamId() {
		return streamId;
	}
	@Override
	public int getSize() {
		return size;
	}
	@Override
	public int getDeltaTime() {
		return deltaTime;
	}
	@Override
	public IRtmpHeader switchTo(HeaderType type) throws Exception {
		if(type == headerType) {
			return this;
		}
		RtmpHeader result = null;
		switch(type) {
		case Type0:
			result = new Type0();
			break;
		case Type1:
			result = new Type1();
			break;
		case Type2:
			result = new Type2();
			break;
		case Type3:
			result = new Type3();
			break;
		default:
			throw new Exception();
		}
		result.csId        = csId;
		result.deltaTime   = deltaTime;
		result.messageType = messageType;
		result.size        = size;
		result.streamId    = streamId;
		result.time        = time;
		return result;
	}
	protected void setMessageType(MessageType type) {
		this.messageType = type;
	}
	@Override
	public void setTime(long time) {
		this.time = time;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setStreamId(int id) {
		this.streamId = id;
	}
	protected void setDeltaTime(int deltaTime) {
		this.deltaTime = deltaTime;
	}
	protected ByteBuffer getHeaderTypeChunkStreamIdBytes() {
		ByteBuffer result = null;
		if(csId <= 63) {
			result = ByteBuffer.allocate(1);
			result.put((byte)((headerType.intValue() << 6) | csId));
		}
		else if(csId <= 320) {
			result = ByteBuffer.allocate(2);
			result.put((byte)(headerType.intValue() << 6));
			result.put((byte)(csId - 64));
		}
		else {
			result = ByteBuffer.allocate(3);
			result.put((byte)((headerType.intValue() << 6) | 1));
			result.putShort((short)(csId - 64));
		}
		result.flip();
		return result;
	}
}
