/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.netty;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.rtmp.command.CommandType;
import com.ttProject.rtmp.message.IRtmpMessage;
import com.ttProject.rtmp.message.type.Acknowledgement;
import com.ttProject.rtmp.message.type.Amf0Command;
import com.ttProject.rtmp.message.type.SetPeerBandwidth;
import com.ttProject.rtmp.message.type.UserControlMessage;
import com.ttProject.rtmp.message.type.WindowAcknowledgementSize;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ClientHandler
 * @author taktod
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
	private Logger logger = Logger.getLogger(ClientHandler.class);
	private int bytesReadWindow = 2500000;
	@SuppressWarnings("unused")
	private int bytesWrittenWindow = 2500000;
//	private int transactionId = 2; // for unique transactionId, increment to use.
	private Map<Integer, CommandType> transactionMap = new HashMap<Integer, CommandType>();
	private long bytesRead = 0;
	private long bytesReadAcked = 0;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(!(msg instanceof IRtmpMessage)) {
			logger.info("not IRtmpMessage object, invalid.");
			return;
		}
		IRtmpMessage message = (IRtmpMessage)msg;
		bytesRead += message.getHeader().getSize();
		if(bytesRead - bytesReadAcked >= bytesReadWindow) {
			// need to send acknowledgement for server.
			Acknowledgement acknowledgement = new Acknowledgement();
			acknowledgement.setSize((int)bytesRead);
			ctx.writeAndFlush(acknowledgement);
			bytesReadAcked = bytesRead;
		}
		switch(message.getHeader().getMessageType()) {
		case ABORT_MESSAGE:
		case ACKNOWLEDGEMENT:
		case AGGREGATE_MESSAGE:
			break;
		case AMF0_COMMAND:
			Amf0Command amf0Command = (Amf0Command)message;
			CommandType type = amf0Command.getCommandType();
			switch(type) {
			case Result:
				CommandType resType = transactionMap.get(amf0Command.getTransactionId());
				if(resType == null) {
					super.channelRead(ctx, msg);
					return;
				}
				switch(resType) {
				default:
					break;
				}
				break;
			default:
				break;
			}
			break;
		case AMF0_DATA_MESSAGE:
		case AMF0_SHARED_OBJECT_MESSAGE:
		case AMF3_COMMAND:
		case AMF3_DATA_MESSAGE:
		case AMF3_SHARED_OBJECT_MESSAGE:
		case AUDIO_MESSAGE:
			break;
		case SET_CHUNK_SIZE:
			// nothing to do, already done with readDecoder.
			break;
		case USER_CONTROL_MESSAGE:
			UserControlMessage userControlMessage = (UserControlMessage)message;
//			logger.info(userControlMessage.getType());
			switch(userControlMessage.getType()) {
			case BUFFER_EMPTY:
//				logger.info("buffer empty for stream.");
				break;
			case BUFFER_FULL:
//				logger.info("buffer full for stream.");
				break;
			case CLIENT_BUFFER_LENGTH:
				break;
			case PING:
				// reply pong for ping.
				int time = userControlMessage.getTime();
				UserControlMessage pong = new UserControlMessage();
				pong.setType(UserControlMessage.Type.PONG);
				pong.setTime(time);
				ctx.writeAndFlush(pong);
				break;
			case PONG:
				break;
			case PING_SWF_VERIFICATION:
			case PONG_SWF_VERIFICATION:
				break;
			case RECORDED_STREAM:
			case STREAM_BEGIN:
			case STREAM_DRY:
			case STREAM_EOF:
				break;
			}
			break;
		case VIDEO_MESSAGE:
			break;
		case WINDOW_ACKNOWLEDGEMENT_SIZE:
			WindowAcknowledgementSize windowAcknowledgementSize = (WindowAcknowledgementSize)message;
			bytesReadWindow = windowAcknowledgementSize.getAcknowledgementSize();
			break;
		case SET_PEER_BANDWIDTH:
			SetPeerBandwidth setPeerBandwidth = (SetPeerBandwidth)message;
			bytesWrittenWindow = setPeerBandwidth.getAcknowledgeWindowSize();
			break;
		default:
			break;
		}
		super.channelRead(ctx, msg);
	}
}
