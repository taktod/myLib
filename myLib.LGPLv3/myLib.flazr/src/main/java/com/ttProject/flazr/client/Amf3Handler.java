/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.client;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.CommandAmf0;
import com.ttProject.flazr.rtmp.message.CommandAmf3;

/**
 * for extra code of Amf3
 * @author taktod
 */
public class Amf3Handler extends SimpleChannelHandler {
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(Amf3Handler.class);
	/** mode */
	private Mode mode = Mode.AMF0;
	/**
	 * enum for working mode.
	 * @author taktod
	 */
	private enum Mode {
		AMF0,
		AMF3;
	};
	/** hold the connectTransactionId to get result. */
	private int connectTransactionId = -1;
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent me)
			throws Exception {
		Object msg = me.getMessage();
		if(msg instanceof RtmpMessage) {
			final RtmpMessage message = (RtmpMessage)me.getMessage();
			switch(message.getHeader().getMessageType()) {
			case COMMAND_AMF0:
				// check the connect.(to get the result object.)
				Command command = (Command)message;
				String name = command.getName();
				if(name.equals("connect")) {
					connectTransactionId = command.getTransactionId();
				}
				if(mode == Mode.AMF3) {
					// change the command mode. from amf0 to amf3
					Channels.write(ctx, me.getFuture(), new CommandAmf3((CommandAmf0)command));
					return;
				}
				break;
			default:
				break;
			}
		}
		super.writeRequested(ctx, me); // goto next writer
	}
	/**
	 * messageReceived.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent me)
			throws Exception {
		Object msg = me.getMessage();
		if(msg instanceof RtmpMessage) {
			final RtmpMessage message = (RtmpMessage)me.getMessage();
			switch(message.getHeader().getMessageType()) {
			case COMMAND_AMF0:
			case COMMAND_AMF3:
				Command command = (Command) message;
				String name = command.getName();
				if(name.equals("_result") && command.getTransactionId() == connectTransactionId) {
					final Map<String, Object> data = (Map<String, Object>)command.getArg(0);
					Object objectEncoding = data.get("objectEncoding");
					if(objectEncoding != null && ((Double)objectEncoding).intValue() == 3) {
						mode = Mode.AMF3;
					}
				}
				break;
			default:
				break;
			}
		}
		super.messageReceived(ctx, me); // goto next messageReceived.
	}
}
