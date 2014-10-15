/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.rtmp.message;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.amf.Amf0Object;
import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.CommandAmf0;
import com.ttProject.container.flv.amf.Amf0Value;
import com.ttProject.flazr.unit.Amf0ObjectManager;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * CommandAmf0Ex
 * original flazr's CommandAmf0 cannot handle Amf3 extra message.
 * in order to deal with such message, I make CommandAmf0Ex.
 * @author taktod
 */
public class CommandAmf0Ex implements RtmpMessage {
	private static final Logger logger = LoggerFactory.getLogger(CommandAmf0Ex.class);
	private final RtmpHeader header;
	private String name;
	private Integer transactionId;
	private Object object;
	private Object[] args;
	private final Amf0ObjectManager amf0ObjectManager = new Amf0ObjectManager();
	/**
	 * constructor
	 * @param header
	 * @param in
	 */
	public CommandAmf0Ex(RtmpHeader header, ChannelBuffer in) {
		this.header = header;
		decode(in);
	}
	@Override
	public void decode(ChannelBuffer in) {
		int length = in.readableBytes();
		byte[] bytes = new byte[length];
		in.readBytes(bytes);
		try {
			IReadChannel channel = new ByteReadChannel(bytes);
			name = (String)Amf0Value.getValueObject(channel);
			transactionId = ((Double)Amf0Value.getValueObject(channel)).intValue();
			object = Amf0Value.getValueObject(channel);
			List<Object> list = new ArrayList<Object>();
			while(channel.size() > channel.position()) {
				Object data = Amf0Value.getValueObject(channel);
				if(data instanceof Integer) {
					Integer i = (Integer)data;
					list.add(i.doubleValue());
				}
				else {
					list.add(data);
				}
			}
			args = list.toArray();
		}
		catch(Exception e) {
			logger.error("failed to parse data.", e);
		}
	}
	@Override
	public ChannelBuffer encode() {
		throw new RuntimeException("encode is not supported now.");
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
