/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.header;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;

import com.ttProject.rtmp.decode.Message;
import com.ttProject.rtmp.header.type.Type0;
import com.ttProject.rtmp.header.type.Type1;
import com.ttProject.rtmp.header.type.Type2;
import com.ttProject.rtmp.header.type.Type3;
import com.ttProject.rtmp.message.MessageType;

/**
 * HeaderFactory
 * @author taktod
 * factory class for IRtmpHeader
 */
public class HeaderFactory {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(HeaderFactory.class);
	private static HeaderFactory instance = new HeaderFactory();
	/**
	 * constructor
	 */
	private HeaderFactory() {
	}
	/**
	 * singleton getter.
	 * @return
	 */
	public static synchronized HeaderFactory getInstance() {
		return instance;
	}
	/**
	 * factory method for header.
	 * @param in
	 * @param messages
	 * @return
	 */
	public IRtmpHeader getHeader(ByteBuf in, Message[] messages) {
		// readHeaderType and chunkStreamId
		int firstByte = in.readByte();
		int csId = 0;
		int headerTypeValue = (firstByte >> 6) & 0x03;
		switch(firstByte & 0x3F) {
		case 0:
			csId = in.readByte() & 0xFF;
			break;
		case 1:
			csId = in.readShort() & 0xFFFF;
			break;
		default:
			csId = firstByte & 0x3F;
			break;
		}
		// setup data.
		RtmpHeader header = null;
		switch(HeaderType.getType(headerTypeValue)) {
		case Type0:
			{
				header = new Type0();
				header.setCsId(csId);
				header.setTime(in.readMedium());
				header.setSize(in.readMedium());
				header.setMessageType(MessageType.getType(in.readByte()));
				// これはなぜかlittleEndianらしい。
				int a,b,c,d;
				a = in.readByte();
				b = in.readByte();
				c = in.readByte();
				d = in.readByte();
				header.setStreamId((d << 24) | (c << 16) | (b << 8) | a);
				if(header.getTime() == IRtmpHeader.MAX_TIME) {
					header.setTime(in.readInt());
				}
			}
			break;
		case Type1:
			{
				IRtmpHeader prevHeader = messages[csId].getHeader();
				header = new Type1();
				header.setCsId(csId);
				header.setDeltaTime(in.readMedium());
				header.setSize(in.readMedium());
				header.setMessageType(MessageType.getType(in.readByte()));
				header.setStreamId(prevHeader.getStreamId());
				if(header.getDeltaTime() == IRtmpHeader.MAX_TIME) {
					header.setDeltaTime(in.readInt());
				}
			}
			break;
		case Type2:
			{
				IRtmpHeader prevHeader = messages[csId].getHeader();
				header = new Type2();
				header.setCsId(csId);
				header.setDeltaTime(in.readMedium());
				header.setSize(prevHeader.getSize());
				header.setMessageType(prevHeader.getMessageType());
				header.setStreamId(prevHeader.getStreamId());
				if(header.getDeltaTime() == IRtmpHeader.MAX_TIME) {
					header.setDeltaTime(in.readInt());
				}
			}
			break;
		case Type3:
			{
				IRtmpHeader prevHeader = messages[csId].getHeader();
				header = new Type3();
				header.setCsId(csId);
				header.setDeltaTime(prevHeader.getDeltaTime());
				header.setSize(prevHeader.getSize());
				header.setMessageType(prevHeader.getMessageType());
				header.setStreamId(prevHeader.getStreamId());
			}
			break;
		}
		return header;
	}
	@Deprecated
	public IRtmpHeader getHeader(MessageType type, int time, int size) {
		RtmpHeader header = (RtmpHeader)getHeader(type);
		header.setSize(size);
		header.setTime(time);
		return header;
	}
	public IRtmpHeader getHeader(MessageType type) {
		Type0 header = new Type0();
		header.setCsId(MessageType.getDefaultCsId(type));
		header.setMessageType(type);
		return header;
	}
}
