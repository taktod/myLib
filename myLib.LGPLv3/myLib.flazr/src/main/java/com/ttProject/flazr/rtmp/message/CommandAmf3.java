/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.rtmp.message;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.amf.Amf0Object;
import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.CommandAmf0;
import com.flazr.rtmp.message.MessageType;
import com.ttProject.container.flv.amf.Amf0Value;
import com.ttProject.flazr.unit.Amf0ObjectManager;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * CommandAmf3
 * 
 * @author taktod
 */
public class CommandAmf3 implements RtmpMessage {
	private static final Logger logger = LoggerFactory.getLogger(CommandAmf3.class);
	private final RtmpHeader header;
	private String name;
	private Integer transactionId;
	private Object object;
	private Object[] args;
	private ChannelBuffer resBuffer = null;
	private final Amf0ObjectManager amf0ObjectManager = new Amf0ObjectManager();
	/**
	 * constructor
	 * @param header
	 * @param in
	 */
	public CommandAmf3(RtmpHeader header, ChannelBuffer in) {
		this.header = header;
		decode(in);
	}
	/**
	 * constructor
	 * @param amf0
	 */
	public CommandAmf3(CommandAmf0 amf0) {
		this.header = amf0.getHeader();
		header.setMessageType(MessageType.COMMAND_AMF3);
		ByteBuffer data = amf0.encode().toByteBuffer();
		ChannelBuffer buffer = ChannelBuffers.buffer(data.remaining() + 1);
		buffer.writeByte((byte)0x00);
		buffer.writeBytes(data);
		resBuffer = buffer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void decode(ChannelBuffer in) {
		int length = in.readableBytes();
		byte[] bytes = new byte[length];
		in.readBytes(bytes);
		try {
			IReadChannel channel = new ByteReadChannel(bytes);
			// check the first byte, if 0x00 do amf0, if 0x11 do amf3.
			switch(BufferUtil.safeRead(channel, 1).get()) {
			case 0x00:
				name = (String)Amf0Value.getValueObject(channel);
				transactionId = ((Double)Amf0Value.getValueObject(channel)).intValue();
				object = Amf0Value.getValueObject(channel);
				List<Object> list = new ArrayList<Object>();
				while(channel.size() > channel.position()) {
					list.add(Amf0Value.getValueObject(channel));
				}
				args = list.toArray();
				break;
			case 0x11:
				throw new Exception("unexpect data is comming. taktod wanna sample for this case. please contact me.");
			}
		}
		catch(Exception e) {
			logger.error("failed to parse data.", e);
		}
	}
	@Override
	public ChannelBuffer encode() {
		if(resBuffer == null) {
			throw new RuntimeException("encode is not supported now.");
		}
		return resBuffer;
	}
	@Override
	public RtmpHeader getHeader() {
		return header;
	}
	public String getName() {
		return name;
	}
	public Integer getTransactionId() {
		return transactionId;
	}
	public Object getObject() {
		return object;
	}
	public Object getArg(int index) {
		return args[index];
	}
	public int getArgCount() {
		if(args == null) {
			return 0;
		}
		return args.length;
	}
	public CommandAmf0 transform() {
		if(object == null || object instanceof com.ttProject.container.flv.amf.Amf0Object) {
			CommandAmf0 command0 = new CommandAmf0(transactionId, name, (Amf0Object)amf0ObjectManager.toFlazrObject(object), args);
			return command0;
		}
		throw new RuntimeException("base object is not amf0Object.");
	}
}
