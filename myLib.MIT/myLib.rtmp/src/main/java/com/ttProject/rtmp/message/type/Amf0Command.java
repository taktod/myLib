/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message.type;

import java.nio.ByteBuffer;

import com.ttProject.container.flv.amf.Amf0Value;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.rtmp.command.CommandType;
import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.MessageType;
import com.ttProject.rtmp.message.RtmpMessage;
import com.ttProject.util.BufferUtil;

import io.netty.buffer.ByteBuf;

/**
 * Amf0Command
 * @author taktod
 */
public class Amf0Command extends RtmpMessage {
	private String commandName;
	private int transactionId;
	private Object object;
	private Object extra;
	/**
	 * constructor
	 */
	public Amf0Command() {
		super();
		transactionId = 0;
		object = null;
		extra = null;
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public Amf0Command(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageType getMessageType() {
		return MessageType.AMF0_COMMAND;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		ByteBuffer commandNameBuffer = Amf0Value.getValueBuffer(commandName);
		ByteBuffer transactionIdBuffer = Amf0Value.getValueBuffer(transactionId);
		ByteBuffer objectBuffer = Amf0Value.getValueBuffer(object);
		ByteBuffer extraBuffer = null;
		if(extra != null) {
			extraBuffer = Amf0Value.getValueBuffer(extra);
		}
		return BufferUtil.connect(commandNameBuffer, transactionIdBuffer, objectBuffer, extraBuffer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(ByteBuf in) throws Exception {
		IReadChannel channel = new ByteReadChannel(in.nioBuffer());
		Object data = Amf0Value.getValueObject(channel);
		commandName = (String)data;
		data = Amf0Value.getValueObject(channel);
		transactionId = ((Double)data).intValue();
		data = Amf0Value.getValueObject(channel);
		object = data;
		if(channel.position() < channel.size()) {
			data = Amf0Value.getValueObject(channel);
			extra = data;
		}
		else {
			extra = null;
		}
	}
	public void setCommandType(CommandType type) {
		this.commandName = type.strValue();
	}
	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}
	public void setTransactionId(int id) {
		this.transactionId = id;
	}
	public CommandType getCommandType() {
		return CommandType.getValue(commandName);
	}
	public void setObject(Object data) {
		this.object = data;
	}
	public void setExtra(Object data) {
		this.extra = data;
	}
	public String getCommandName() {
		return commandName;
	}
	public int getTransactionId() {
		return transactionId;
	}
	public Object getObject() {
		return object;
	}
	public Object getExtra() {
		return extra;
	}
}
