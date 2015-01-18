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
 * SetPeerBandwidth
 * @author taktod
 */
public class SetPeerBandwidth extends RtmpMessage {
	public static enum Type {
		HARD(0),
		SOFT(1),
		DYNAMIC(2);
		private final int value;
		private Type(int value) {
			this.value = value;
		}
		public int intValue() {
			return value;
		}
		public static Type getType(int value) throws Exception {
			for(Type t : values()) {
				if(t.intValue() == value) {
					return t;
				}
			}
			throw new Exception("out of range.");
		}
	}
	private int acknowledgeWindowSize;
	private Type limitType;
	/**
	 * constructor
	 */
	public SetPeerBandwidth() {
		super();
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public SetPeerBandwidth(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(5);
		buffer.putInt(acknowledgeWindowSize);
		buffer.put((byte)limitType.intValue());
		buffer.flip();
		return buffer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(ByteBuf in) throws Exception {
		acknowledgeWindowSize = in.readInt();
		limitType = Type.getType(in.readByte());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageType getMessageType() {
		return MessageType.SET_PEER_BANDWIDTH;
	}
	public int getAcknowledgeWindowSize() {
		return acknowledgeWindowSize;
	}
	public void setAcknowledgeWindowSize(int acknowledgeWindowSize) {
		this.acknowledgeWindowSize = acknowledgeWindowSize;
	}
	public Type getLimitType() {
		return limitType;
	}
	public void setLimitType(Type limitType) {
		this.limitType = limitType;
	}
}
