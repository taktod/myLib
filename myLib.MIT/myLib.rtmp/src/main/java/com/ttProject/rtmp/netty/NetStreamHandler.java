/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.netty;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.amf.Amf0Object;
import com.ttProject.rtmp.NetConnection;
import com.ttProject.rtmp.NetStream;
import com.ttProject.rtmp.command.Amf0;
import com.ttProject.rtmp.message.IRtmpMessage;
import com.ttProject.rtmp.message.type.Amf0Command;
import com.ttProject.rtmp.message.type.UserControlMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * NetStreamHandler
 * @author taktod
 */
public class NetStreamHandler extends ChannelInboundHandlerAdapter {
	private Logger logger = Logger.getLogger(NetStreamHandler.class);
	private final int transactionId;
	private final NetConnection conn;
	private final NetStream ns;
	private int streamId = -1;
	public NetStreamHandler(NetConnection conn, NetStream ns) {
		this.conn = conn;
		this.ns = ns;
		conn.addLast(this);
		transactionId = conn.writeAndFlush(Amf0.createStream(0));
	}
	@SuppressWarnings("unchecked")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(!(msg instanceof IRtmpMessage)) {
//			logger.info("not IRtmpMessage object, invalid.");
			return;
		}
		IRtmpMessage message = (IRtmpMessage)msg;
		if(streamId != -1 && streamId != message.getHeader().getStreamId()) {
			logger.info("not for this handler.");
			super.channelRead(ctx, msg);
			return;
		}
		switch(message.getHeader().getMessageType()) {
		case AMF0_COMMAND:
			Amf0Command command = (Amf0Command)message;
			switch(command.getCommandType()) {
			case Result:
				if(command.getTransactionId() == transactionId) {
					streamId = ((Double)command.getExtra()).intValue();
					switch (ns.getType()) {
					case Play:
						ctx.write(Amf0.receiveAudio(true));
						ctx.write(Amf0.receiveVideo(true));
						// bufferLength
						UserControlMessage setBufferLength = new UserControlMessage();
						setBufferLength.setType(UserControlMessage.Type.CLIENT_BUFFER_LENGTH);
						setBufferLength.setStreamId(streamId);
						setBufferLength.setBufferLength(conn.getBufferLength());
						ctx.write(setBufferLength);
						ctx.write(Amf0.play(ns.getStreamName(), streamId));
						ctx.flush();
						break;
					case Publish:
						throw new RuntimeException("publish is underconstruct.");
					default:
						break;
					}
				}
				break;
			case OnStatus:
				{
					ns.onStatusEvent((Amf0Object<String, Object>)command.getExtra());
				}
				break;
			default:
				break;
			}
			break;
		case USER_CONTROL_MESSAGE:
			UserControlMessage userControlMessage = (UserControlMessage)message;
			logger.info(userControlMessage.getType());
			// userControlMessage
			switch(userControlMessage.getType()) {
			case BUFFER_EMPTY:
				{
					// TODO this message if not for NetStream.Buffer.Empty?
					// streamId of rtmpHeader is different.
/*					Amf0Object<String, Object> obj = new Amf0Object<String, Object>();
					obj.put("level", "status");
					obj.put("code", "NetStream.Buffer.Empty");
					ns.onStatusEvent(obj);*/
				}
				break;
			case BUFFER_FULL:
				{
/*					Amf0Object<String, Object> obj = new Amf0Object<String, Object>();
					obj.put("level", "status");
					obj.put("code", "NetStream.Buffer.Full");
					ns.onStatusEvent(obj);*/
				}
				break;
			case STREAM_BEGIN:
			case STREAM_DRY:
			case STREAM_EOF:
			default:
				break;
			}
			break;
		case AUDIO_MESSAGE:
		case VIDEO_MESSAGE:
		case AGGREGATE_MESSAGE:
			{
				ns.onMediaReceived(message); 
			}
			break;
		default:
			break;
		}
		super.channelRead(ctx, msg);
	}
}
