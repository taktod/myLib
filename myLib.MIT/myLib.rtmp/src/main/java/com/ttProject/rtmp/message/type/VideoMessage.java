/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.message.type;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.MessageType;
import com.ttProject.rtmp.message.RtmpMessage;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.util.BufferUtil;

/**
 * VideoMessage
 * @author taktod
 */
public class VideoMessage extends RtmpMessage {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(VideoMessage.class);
	private Bit4 frameType;
	private Bit4 codecId;
	@SuppressWarnings("unused")
	private ByteBuffer getFrameData; // frameデータっぽい frameの形にしておきたいところ。
	/**
	 * constructor
	 */
	public VideoMessage() {
		super();
		frameType = null;
		codecId = null;
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public VideoMessage(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		// TODO make it.
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(ByteBuf in) throws Exception {
		frameType = new Bit4();
		codecId = new Bit4();
		IReadChannel channel = new ByteReadChannel(in.nioBuffer());
		BitLoader loader = new BitLoader(channel);
		loader.load(frameType, codecId);
		getFrameData = BufferUtil.safeRead(channel, channel.size() - channel.position());
//		logger.info(frameType.get());
//		logger.info(FlvCodecType.getVideoCodecType(codecId.get()));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageType getMessageType() {
		return MessageType.VIDEO_MESSAGE;
	}
}
