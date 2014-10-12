/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.rtmp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpDecoder.DecoderState;
import com.flazr.rtmp.message.ChunkSize;
import com.flazr.rtmp.message.MessageType;
import com.ttProject.flazr.rtmp.message.CommandAmf0Ex;
import com.ttProject.flazr.rtmp.message.CommandAmf3;
import com.ttProject.flazr.rtmp.message.MetadataAmf3;
import com.ttProject.util.HexUtil;

/**
 * extend rtmpDecoder to deal with amf3 messages.
 * @author taktod
 */
public class RtmpDecoderEx extends ReplayingDecoder<DecoderState> {
	/** logger */
	private static final Logger logger = LoggerFactory.getLogger(RtmpDecoderEx.class);
	/**
	 * constructor
	 */
	public RtmpDecoderEx() {
		super(DecoderState.GET_HEADER);
	}
	// tmp data.
	private RtmpHeader header;
	private int channelId;
	private ChannelBuffer payload;
	private int chunkSize = 128;

	private final RtmpHeader[] incompleteHeaders = new RtmpHeader[RtmpHeader.MAX_CHANNEL_ID];
	private final ChannelBuffer[] incompletePayloads = new ChannelBuffer[RtmpHeader.MAX_CHANNEL_ID];
	private final RtmpHeader[] completedHeaders = new RtmpHeader[RtmpHeader.MAX_CHANNEL_ID];

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer in, DecoderState state) throws Exception {
		switch(state) {
		case GET_HEADER:
			header = new RtmpHeaderEx(in, incompleteHeaders);
			channelId = header.getChannelId();
			if(incompletePayloads[channelId] == null) {
				incompleteHeaders[channelId] = header;
				incompletePayloads[channelId] = ChannelBuffers.buffer(header.getSize());
			}
			payload = incompletePayloads[channelId];
			checkpoint(DecoderState.GET_PAYLOAD);
		case GET_PAYLOAD:
			final byte[] bytes = new byte[Math.min(payload.writableBytes(), chunkSize)];
			in.readBytes(bytes);
			payload.writeBytes(bytes);
			checkpoint(DecoderState.GET_HEADER);
			// check more chunk?
			if(payload.writable()) {
				return null;
			}
			// nomore
			incompletePayloads[channelId] = null;
			try {
				if(header.getMessageType() == MessageType.SHARED_OBJECT_AMF3) {
					logger.warn("unknown order, just ignore.(will shutdown process.)");
					logger.info("type:{}, dump:{}", header.getMessageType(), HexUtil.toHex(bytes, true));
					return null;
				}
				final RtmpHeader prevHeader = completedHeaders[channelId];
				// timestamp injection for the test of overflow for time.
//				if(header.isLarge()) {
//					header.setTime(header.getTime() + 16777000);
//				}
				if(!header.isLarge()) {
					header.setTime(prevHeader.getTime() + header.getDeltaTime());
				}
				final RtmpMessage message = MessageTypeDecode(header, payload);
				if(header.isChunkSize()) {
					final ChunkSize csMessage = (ChunkSize) message;
					chunkSize = csMessage.getChunkSize();
				}
				completedHeaders[channelId] = header;
				return message;
			}
			catch (Exception e) {
				logger.error("decode error:", e);
				logger.info("------------------ type:{} -----------------", header.getMessageType());
				logger.info(HexUtil.toHex(bytes, true));
				return null;
			}
		default:
			throw new RuntimeException("unexpected decoder state:" + state);
		}
	}
	/**
	 * decode rtmpmessages.
	 * @param header
	 * @param payload
	 * @return
	 */
	private RtmpMessage MessageTypeDecode(RtmpHeader header, ChannelBuffer payload) {
		switch(header.getMessageType()) {
		case METADATA_AMF3:
			MetadataAmf3 metadata3 = new MetadataAmf3(header, payload);
			return metadata3.transform(); // force to use amf0 for clientHandler
		case COMMAND_AMF3:
			CommandAmf3 command3 = new CommandAmf3(header, payload);
			return command3.transform(); // force to use amf0 for clientHandler
		case COMMAND_AMF0:
			// need extra command amf0 here.
			CommandAmf0Ex command0 = new CommandAmf0Ex(header, payload);
			return command0.transform(); // force to use amf0 for clientHandler
		default:
			return MessageType.decode(header, payload);
		}
	}
}
