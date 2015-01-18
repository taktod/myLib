/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.header;

import java.nio.ByteBuffer;

import com.ttProject.rtmp.message.MessageType;

/**
 * IRtmpHeader
 * @author taktod
 */
public interface IRtmpHeader {
	public static final int MAX_CHANNEL_ID = 65600;
	public static final int MAX_TIME = 0xFFFFFF;
	public boolean isMedia();
	public boolean isMetaData();
	public boolean isAggregate();
	public boolean isAudio();
	public boolean isVideo();
	public boolean isControl();
	public boolean isChunkSize();
	public void setCsId(int channelId);
	public void setTime(long time);
	public void setSize(int size);
	public void setStreamId(int streamId);
	public int  getCsId();
	public long getTime();
	public int  getDeltaTime();
	public int  getStreamId();
	public int  getSize();
	public MessageType getMessageType();
	public ByteBuffer getData();
	public IRtmpHeader switchTo(HeaderType type) throws Exception;
}
