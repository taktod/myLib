/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.netty;

import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.rtmp.decode.Message;
import com.ttProject.rtmp.decode.MessageFactory;
import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.IRtmpMessage;
import com.ttProject.rtmp.message.type.AggregateMessage;
import com.ttProject.rtmp.message.type.Amf0Command;
import com.ttProject.rtmp.message.type.Amf0DataMessage;
import com.ttProject.rtmp.message.type.AudioMessage;
import com.ttProject.rtmp.message.type.SetChunkSize;
import com.ttProject.rtmp.message.type.SetPeerBandwidth;
import com.ttProject.rtmp.message.type.UserControlMessage;
import com.ttProject.rtmp.message.type.VideoMessage;
import com.ttProject.rtmp.message.type.WindowAcknowledgementSize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

/**
 * rtmpDecoder
 * @author taktod
 * decode rtmp message into IRtmpMessage object.
 */
public class RtmpDecoder extends ReplayingDecoder<RtmpDecoder.DecoderStatus> {
	public static enum DecoderStatus {
		Header,
		Body
	};
	/**
	 * constructor
	 */
	public RtmpDecoder() {
		super(DecoderStatus.Header);
	}
	private Logger logger = Logger.getLogger(RtmpDecoder.class);
	private final MessageFactory factory = new MessageFactory();
	private Message message;
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf,
		List<Object> out) throws Exception {
		switch(state()) {
		case Header:
			message = factory.getMessage(buf);
			checkpoint(DecoderStatus.Body);
			break;
		case Body:
			int size = factory.getBufSize(message);
			if(size > buf.readableBytes()) {
				// need more data for rtmp packet.
				return;
			}
			byte[] bytes = new byte[size];
			buf.readBytes(bytes);
			message.getBody().writeBytes(bytes);
			checkpoint(DecoderStatus.Header);
			if(!message.isComplete() || size == 0) {
				// need more rtmp packet.
				return;
			}
			// get header.
			IRtmpHeader header = message.getHeader();
			// make type message.
			IRtmpMessage message = null;
			switch(header.getMessageType()) {
			case WINDOW_ACKNOWLEDGEMENT_SIZE:
				message = new WindowAcknowledgementSize(header, this.message.getBody());
				break;
			case SET_PEER_BANDWIDTH:
				message = new SetPeerBandwidth(header, this.message.getBody());
				break;
			case SET_CHUNK_SIZE:
				SetChunkSize chunkSize = new SetChunkSize(header, this.message.getBody());
				factory.setChunkSize(chunkSize.getChunkSize());
				message = chunkSize;
				break;
			case AMF0_COMMAND:
				message = new Amf0Command(header, this.message.getBody());
				break;
			case AMF0_DATA_MESSAGE:
				message = new Amf0DataMessage(header, this.message.getBody());
				break;
			case USER_CONTROL_MESSAGE:
				message = new UserControlMessage(header, this.message.getBody());
				break;
			case VIDEO_MESSAGE:
				message = new VideoMessage(header, this.message.getBody());
				break;
			case AUDIO_MESSAGE:
				message = new AudioMessage(header, this.message.getBody());
				break;
			case AGGREGATE_MESSAGE:
				message = new AggregateMessage(header, this.message.getBody());
				break;
			case ABORT_MESSAGE:
			case ACKNOWLEDGEMENT:
			case AMF0_SHARED_OBJECT_MESSAGE:
			case AMF3_COMMAND:
			case AMF3_DATA_MESSAGE:
			case AMF3_SHARED_OBJECT_MESSAGE:
				logger.info("need to make.:" + header.getMessageType());
				break;
			default:
				throw new RuntimeException("undefined:" + header.getMessageType());
			}
			// if message = null, throws NullPointerException.
			ctx.fireChannelRead(message);
			break;
		}
	}
}
