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

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.MessageType;
import com.ttProject.rtmp.message.RtmpMessage;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.util.BufferUtil;

/**
 * AudioMessage
 * @author taktod
 */
public class AudioMessage extends RtmpMessage {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(AudioMessage.class);
	private Bit4 codecId;
	private Bit2 sampleRate;
	private Bit1 bitCount;
	private Bit1 channels;
	@SuppressWarnings("unused")
	private ByteBuffer getFrameData;
	/**
	 * constructor
	 */
	public AudioMessage() {
		super();
		codecId = null;
		sampleRate = null;
		bitCount = null;
		channels = null;
	}
	/**
	 * constructor
	 * @param header
	 * @param in
	 * @throws Exception
	 */
	public AudioMessage(IRtmpHeader header, ByteBuf in) throws Exception {
		super(header, in);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageType getMessageType() {
		return MessageType.AUDIO_MESSAGE;
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
		codecId = new Bit4();
		sampleRate = new Bit2();
		bitCount = new Bit1();
		channels = new Bit1();
		IReadChannel channel = new ByteReadChannel(in.nioBuffer());
		BitLoader loader = new BitLoader(channel);
		loader.load(codecId, sampleRate, bitCount, channels);
		getFrameData = BufferUtil.safeRead(channel, channel.size() - channel.position());
//		logger.info(FlvCodecType.getAudioCodecType(codecId.get()));
	}
}
