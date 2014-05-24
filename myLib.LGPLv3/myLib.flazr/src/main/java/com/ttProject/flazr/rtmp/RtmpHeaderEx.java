/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.rtmp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.message.MessageType;
import com.flazr.util.Utils;

/**
 * rtmpHeaderの処理を上書きする動作
 * @author taktod
 *
 */
public class RtmpHeaderEx extends RtmpHeader {
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(RtmpHeaderEx.class);
	private Type headerType;
	private int channelId;
	private int deltaTime;
	private int time;
	private int size;
	private MessageType messageType;
	private int streamId;
	
	public RtmpHeaderEx(ChannelBuffer in, RtmpHeader[] incompleteHeaders) {
		this(MessageType.CONTROL);
		final int firstByteInt = in.readByte();
		final int typeAndChannel;
		final int headerTypeInt;
		if((firstByteInt & 0x3F) == 0) {
			typeAndChannel = (firstByteInt & 0xFF) << 8 | (in.readByte() & 0xFF);
			channelId = 64 + (typeAndChannel & 0xFF);
			headerTypeInt = typeAndChannel >> 14;
		}
		else if((firstByteInt & 0x3F) == 1) {
			typeAndChannel = (firstByteInt & 0xFF) << 16 | (in.readByte() & 0xFF) << 8 | (in.readByte() & 0xFF);
			channelId = 64 + ((typeAndChannel >> 8) & 0xFF) + ((typeAndChannel & 0xFF) << 8);
			headerTypeInt = typeAndChannel >> 22;
		}
		else {
			typeAndChannel = firstByteInt & 0xFF;
			channelId = (typeAndChannel & 0x3F);
			headerTypeInt = typeAndChannel >> 6;
		}
		headerType = Type.valueToEnum(headerTypeInt);
		
		final RtmpHeaderEx prevHeader = (RtmpHeaderEx)incompleteHeaders[channelId];
		switch(headerType) {
		case LARGE:
			time = in.readMedium();
			size = in.readMedium();
			messageType = MessageType.valueToEnum(in.readByte());
			streamId = Utils.readInt32Reverse(in);
			if(time == MAX_NORMAL_HEADER_TIME) {
				time = in.readInt();
			}
			break;
		case MEDIUM:
			deltaTime = in.readMedium();
			size = in.readMedium();
			messageType = MessageType.valueToEnum(in.readByte());
			streamId = prevHeader.streamId;
			if(deltaTime == MAX_NORMAL_HEADER_TIME) {
				deltaTime = in.readInt();
			}
			break;
		case SMALL:
			deltaTime = in.readMedium();
			size = prevHeader.size;
			messageType = prevHeader.messageType;
			streamId = prevHeader.streamId;
			if(deltaTime == MAX_NORMAL_HEADER_TIME) {
				deltaTime = in.readInt();
			}
			break;
		case TINY:
			headerType = prevHeader.headerType; // preserve original
			time = prevHeader.time;
			deltaTime = prevHeader.deltaTime;
			size = prevHeader.size;
			messageType = prevHeader.messageType;
			streamId = prevHeader.streamId;
			break;
		}
	}
	public RtmpHeaderEx(MessageType messageType, int time, int size) {
		this(messageType);
		this.time = time;
		this.size = size;
	}
	public RtmpHeaderEx(MessageType messageType) {
		super(messageType);
		this.messageType = messageType;
		headerType = Type.LARGE;
		channelId = messageType.getDefaultChannelId();
	}
	@Override
	public boolean isMedia() {
		switch(messageType) {
		case AUDIO:
		case VIDEO:
		case AGGREGATE:
			return true;
		default:
			return false;
		}
	}
	@Override
	public boolean isMetadata() {
		return messageType == MessageType.METADATA_AMF0 || messageType == MessageType.METADATA_AMF3;
	}
	@Override
	public boolean isAggregate() {
		return messageType == MessageType.AGGREGATE;
	}
	@Override
	public boolean isAudio() {
		return messageType == MessageType.AUDIO;
	}
	@Override
	public boolean isVideo() {
		return messageType == MessageType.VIDEO;
	}
	@Override
	public boolean isLarge() {
		return headerType == Type.LARGE;
	}
	@Override
	public boolean isControl() {
		return messageType == MessageType.CONTROL;
	}
	@Override
	public boolean isChunkSize() {
		return messageType == MessageType.CHUNK_SIZE;
	}
	@Override
	public Type getHeaderType() {
		return headerType;
	}
	@Override
	public void setHeaderType(Type headerType) {
		this.headerType = headerType;
	}
	@Override
	public int getChannelId() {
		return channelId;
	}
	@Override
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	@Override
	public int getTime() {
		return time;
	}
	@Override
	public void setTime(int time) {
		this.time = time;
	}
	@Override
	public int getDeltaTime() {
		return deltaTime;
	}
	@Override
	public void setDeltaTime(int deltaTime) {
		this.deltaTime = deltaTime;
	}
	@Override
	public void setSize(int size) {
		this.size = size;
	}
	@Override
	public int getSize() {
		return size;
	}
	@Override
	public MessageType getMessageType() {
		return messageType;
	}
	@Override
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	@Override
	public int getStreamId() {
		return streamId;
	}
	@Override
	public void setStreamId(int streamId) {
		this.streamId = streamId;
	}
	public void encode(ChannelBuffer out) {
		int val = 0;
		switch(headerType) {
		default:
		case LARGE:
			val = 0;
			break;
		case MEDIUM:
			val = 1;
			break;
		case SMALL:
			val = 2;
			break;
		case TINY:
			val = 3;
			break;
		}
		out.writeBytes(encodeHeaderTypeAndChannel(val, channelId));
		if(headerType == Type.TINY) {
			return;
		}
		final boolean extendedTime;
		if(headerType == Type.LARGE) {
			extendedTime = time >= MAX_NORMAL_HEADER_TIME;
		}
		else {
			extendedTime = deltaTime >= MAX_NORMAL_HEADER_TIME;
		}
		if(extendedTime) {
			out.writeMedium(MAX_NORMAL_HEADER_TIME); 
		}
		else {                                        // LARGE / MEDIUM / SMALL
			out.writeMedium(headerType == Type.LARGE ? time : deltaTime);
		}
		if(headerType != Type.SMALL) {
			out.writeMedium(size);                      // LARGE / MEDIUM
			out.writeByte((byte) messageType.intValue());     // LARGE / MEDIUM
			if(headerType == Type.LARGE) {
				Utils.writeInt32Reverse(out, streamId); // LARGE
			}
		}
		if(extendedTime) {
			out.writeInt(headerType == Type.LARGE ? time : deltaTime);
		}
	}
	public byte[] getTinyHeader() {
		return encodeHeaderTypeAndChannel(Type.TINY.intValue(), channelId);
	}
	private static byte[] encodeHeaderTypeAndChannel(final int headerType, final int channelId) {
		if (channelId <= 63) {
			return new byte[] {(byte) ((headerType << 6) + channelId)};
		}
		else if (channelId <= 320) {
			return new byte[] {(byte) (headerType << 6), (byte) (channelId - 64)};
		}
		else {
			return new byte[] {(byte) ((headerType << 6) | 1),
				(byte) ((channelId - 64) & 0xff), (byte) ((channelId - 64) >> 8)};
		}
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[').append(headerType.ordinal());
		sb.append(' ').append(messageType);
		sb.append(" c").append(channelId);        
		sb.append(" #").append(streamId);        
		sb.append(" t").append(time);
		sb.append(" (").append(deltaTime);
		sb.append(") s").append(size);
		sb.append(']');
		return sb.toString();
	}
}
