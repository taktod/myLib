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

/**
 * UserControlMessage
 * @author taktod
 */
public class UserControlMessage extends RtmpMessage {
	private Type type;
	private int streamId; // streamBegin streamEof streamDry streamIsRecorded setBuffer bufferEmpty BuferFull
	private int bufferLength; // setBuffer
	private int time; // ping pong
	private byte[] bytes; // swfvResponse
	public static enum Type {
		STREAM_BEGIN(0),
		STREAM_EOF(1),
		STREAM_DRY(2),
		CLIENT_BUFFER_LENGTH(3),
		RECORDED_STREAM(4),
//		UNKNOWN5(5),
		PING(6),
		PONG(7),
//		UNKNOWN8(8),
		PING_SWF_VERIFICATION(26),
		PONG_SWF_VERIFICATION(27),
		BUFFER_EMPTY(31),
		BUFFER_FULL(32);
		private final int value;
		private Type(int value) {
			this.value = value;
		}
		public int intValue() {
			return value;
		}
		public static Type getValue(int value) throws Exception {
			for(Type t : values()) {
				if(t.intValue() == value) {
					return t;
				}
			}
			throw new Exception("unknown");
		}
	}
	/**
	 * constructor
	 */
	public UserControlMessage() {
		super();
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public UserControlMessage(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		ByteBuffer result = null;
		switch(type) {
		case BUFFER_EMPTY:
		case BUFFER_FULL:
			result = ByteBuffer.allocate(6);
			result.putShort((short)type.intValue());
			result.putInt(streamId);
			break;
		case CLIENT_BUFFER_LENGTH:
			result = ByteBuffer.allocate(10);
			result.putShort((short)type.intValue());
			result.putInt(streamId);
			result.putInt(bufferLength);
			break;
		case PING:
		case PONG:
			result = ByteBuffer.allocate(6);
			result.putShort((short)type.intValue());
			result.putInt(time);
			break;
		case PING_SWF_VERIFICATION:
		case PONG_SWF_VERIFICATION:
			throw new Exception("swfVerification is under construction.");
		case RECORDED_STREAM:
		case STREAM_BEGIN:
		case STREAM_DRY:
		case STREAM_EOF:
			result = ByteBuffer.allocate(6);
			result.putShort((short)type.intValue());
			result.putInt(streamId);
			break;
		}
		result.flip();
		return result;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(ByteBuf in) throws Exception {
		type = Type.getValue(in.readShort());
		switch(type) {
		case BUFFER_EMPTY:
		case BUFFER_FULL:
			streamId = in.readInt();
			break;
		case CLIENT_BUFFER_LENGTH:
			streamId = in.readInt();
			bufferLength = in.readInt();
			break;
		case PING:
		case PONG:
			time = in.readInt();
			break;
		case PING_SWF_VERIFICATION:
		case PONG_SWF_VERIFICATION:
			throw new Exception("swfVerification is under construction.");
		case RECORDED_STREAM:
		case STREAM_BEGIN:
		case STREAM_DRY:
		case STREAM_EOF:
			streamId = in.readInt();
			break;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageType getMessageType() {
		return MessageType.USER_CONTROL_MESSAGE;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public int getStreamId() {
		return streamId;
	}
	public void setStreamId(int streamId) {
		this.streamId = streamId;
	}
	public int getBufferLength() {
		return bufferLength;
	}
	public void setBufferLength(int bufferLength) {
		this.bufferLength = bufferLength;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
