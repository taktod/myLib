/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.rtmp.message;

import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.MetadataAmf0;
import com.ttProject.container.flv.amf.Amf0Value;
import com.ttProject.container.flv.amf.Amf3Value;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

/**
 * MetadataAmf3
 * TODO now this program deal with only one data for onMetaData order.
 * I need the sample for multipleData.
 * @author taktod
 */
public class MetadataAmf3 implements RtmpMessage {
	/** logger */
	private static final Logger logger = LoggerFactory.getLogger(MetadataAmf3.class);
	/** rtmpHeader */
	private final RtmpHeader header;
	/** data map */
	private Map<String, Object> data = null;
	/** setting name(onMetaData) */
	private String name;
	/**
	 * constructor
	 * @param header
	 * @param in
	 */
	public MetadataAmf3(RtmpHeader header, ChannelBuffer in) {
		this.header = header;
		decode(in);
	}
	/**
	 * rebuild the data from channelBuffer
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void decode(ChannelBuffer in) {
		int length = in.readableBytes();
		byte[] bytes = new byte[length];
		in.readBytes(bytes);
		try {
			IReadChannel channel = new ByteReadChannel(bytes);
			
			// check the first several data.
			// 00 02 00 0A 6F 6E 4D 65 74 61 44 61 74 61 is expected.
			if(BufferUtil.safeRead(channel, 1).get() != 0x00) {
				throw new Exception("first data is expected written as AMF0");
			}
			// treat as amf0
			String amf0Data = (String)Amf0Value.getValueObject(channel);
			if(!"onMetaData".equals(amf0Data)) {
				throw new Exception("header string is not metadata.:" + amf0Data);
			}
			name = amf0Data;
			// 0x11 is expected(amf3)
			if(BufferUtil.safeRead(channel, 1).get() != 0x11) { // can be 0x00 with data amf0?
				throw new Exception("data is expected as AMF3");
			}
			// analyze holding data.
			data = (Map<String, Object>)Amf3Value.getValueObject(channel);
			// TODO could have multiple data?
		}
		catch (Exception e) {
			logger.error("", e);
			logger.error("errorData: {}", HexUtil.toHex(bytes, true));
			throw new RuntimeException("faced unknown format.");
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChannelBuffer encode() {
		throw new RuntimeException("encode is not supported now.");
	}
	/**
	 * ref the header
	 */
	@Override
	public RtmpHeader getHeader() {
		return header;
	}
	/**
	 * name(onMetaData)
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * ref the data.
	 * @return
	 */
	public Map<String, Object> getData() {
		return data;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder("MetadataAmf3");
		data.append(" name:").append(getName());
		data.append(" data:").append(getData());
		return data.toString();
	}
	public MetadataAmf0 transform() {
		MetadataAmf0 metadata0 = new MetadataAmf0(name, data);
		return metadata0;
	}
}
