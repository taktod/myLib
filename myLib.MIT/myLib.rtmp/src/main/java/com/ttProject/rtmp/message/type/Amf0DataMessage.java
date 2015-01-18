/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;

import com.ttProject.container.flv.amf.Amf0Value;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.MessageType;
import com.ttProject.rtmp.message.RtmpMessage;

/**
 * Amf0DataMessage
 * @author taktod
 */
public class Amf0DataMessage extends RtmpMessage {
	private String messageName;
	private List<Object> objectData;
	/**
	 * constructor
	 */
	public Amf0DataMessage() {
		super();
		objectData = new ArrayList<Object>();
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public Amf0DataMessage(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageType getMessageType() {
		return MessageType.AMF0_DATA_MESSAGE;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(ByteBuf in) throws Exception {
		IReadChannel channel = new ByteReadChannel(in.nioBuffer());
		Object data = Amf0Value.getValueObject(channel);
		messageName = (String)data;
		objectData = new ArrayList<Object>();
		while(channel.position() < channel.size()) {
			objectData.add(Amf0Value.getValueObject(channel));
		}
	}
	public String getMessageName() {
		return messageName;
	}
	public List<Object> getObjectData() {
		return objectData;
	}
}
