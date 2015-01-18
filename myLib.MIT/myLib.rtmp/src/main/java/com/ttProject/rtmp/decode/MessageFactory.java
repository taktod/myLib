/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.decode;

import org.apache.log4j.Logger;

import com.ttProject.rtmp.header.HeaderFactory;
import com.ttProject.rtmp.header.IRtmpHeader;

import io.netty.buffer.ByteBuf;

/**
 * MessageFactory
 * @author taktod
 * factory to handle Message obj for RtmpDecoder
 */
public class MessageFactory {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MessageFactory.class);
	private int chunkSize = 128; // default chunk size is 128 bytes, this value can be changed by setChunkSize event.
	private Message[] messages = new Message[IRtmpHeader.MAX_CHANNEL_ID];
	/**
	 * factory method for message.
	 * @param in
	 * @return
	 */
	public Message getMessage(ByteBuf in) {
		// get Header.
		IRtmpHeader header = HeaderFactory.getInstance().getHeader(in, messages);
		// ref the prev message.
		Message targetMessage = messages[header.getCsId()];
		// in the case of no message, make new one.
		if(targetMessage == null || targetMessage.isComplete()) {
			targetMessage = new Message(header, targetMessage);
			messages[header.getCsId()] = targetMessage;
		}
		return targetMessage;
	}
	/**
	 * set chunkSize
	 * @param chunkSize
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	/**
	 * get Buffer size for data.
	 * @param message
	 * @return
	 */
	public int getBufSize(Message message) {
		return Math.min(message.getBody().writableBytes(), chunkSize);
	}
}
