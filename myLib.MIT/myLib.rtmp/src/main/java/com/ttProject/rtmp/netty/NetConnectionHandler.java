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
import com.ttProject.rtmp.command.Amf0;
import com.ttProject.rtmp.command.CommandType;
import com.ttProject.rtmp.message.IRtmpMessage;
import com.ttProject.rtmp.message.type.Amf0Command;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * NetConnectionHandler
 * @author taktod
 */
public class NetConnectionHandler extends ChannelInboundHandlerAdapter {
	private Logger logger = Logger.getLogger(NetConnectionHandler.class);
	private final String tcUrl;
	private final NetConnection nc;
	public NetConnectionHandler(String tcUrl, NetConnection nc) {
		this.tcUrl = tcUrl;
		this.nc = nc;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Amf0Command connect = Amf0.connect(1, tcUrl);
		ctx.writeAndFlush(connect);
		super.channelActive(ctx);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(!(msg instanceof IRtmpMessage)) {
			logger.info("not IRtmpMessage object, invalid.");
			return;
		}
		IRtmpMessage message = (IRtmpMessage)msg;
		switch(message.getHeader().getMessageType()) {
		case AMF0_COMMAND:
			// get the message of NetConnection.Connect.
			Amf0Command amf0Command = (Amf0Command)message;
			CommandType type = amf0Command.getCommandType();
			if(amf0Command.getTransactionId() == 1 && type == CommandType.Result) {
				nc.onStatusEvent((Amf0Object<String, Object>)amf0Command.getExtra());
			}
			break;
		default:
			break;
		}
		super.channelRead(ctx, msg);
	}
}
