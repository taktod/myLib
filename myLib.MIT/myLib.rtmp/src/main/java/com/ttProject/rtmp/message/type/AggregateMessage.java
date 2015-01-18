/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;

import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.MessageType;
import com.ttProject.rtmp.message.RtmpMessage;

/**
 * AggregateMessage
 * @author taktod
 */
public class AggregateMessage extends RtmpMessage {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(AggregateMessage.class);
	@SuppressWarnings("unused")
	private ByteBuffer getFrameData;
	/**
	 * constructor
	 */
	public AggregateMessage() {
		super();
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public AggregateMessage(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageType getMessageType() {
		return MessageType.AGGREGATE_MESSAGE;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		// TODO need to make
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(ByteBuf in) throws Exception {
		getFrameData = in.nioBuffer();
//		logger.info("AggregateMessage");
	}
}
