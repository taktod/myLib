/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.netty;

import java.util.List;

import com.ttProject.rtmp.handshake.RtmpHandshake;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * HandshakeHandler
 * @author taktod
 * netty handler for rtmp handshake
 */
public class HandshakeHandler extends ByteToMessageDecoder {
	private RtmpHandshake handshake = new RtmpHandshake();
	private int flags = 0;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(handshake.clientRequest0());
		ctx.writeAndFlush(handshake.clientRequest1());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf,
			List<Object> out) throws Exception {
		switch(flags) {
		case 0:
			if(buf.readableBytes() >= 1) {
				handshake.serverResponse0(buf.readBytes(1));
				flags = 1;
			}
			break;
		case 1:
			if(buf.readableBytes() >= RtmpHandshake.HANDSHAKE_SIZE) {
				handshake.serverResponse1(buf.readBytes(RtmpHandshake.HANDSHAKE_SIZE));
				ctx.writeAndFlush(handshake.clientRequest2());
				flags = 2;
			}
			break;
		case 2:
			if(buf.readableBytes() >= RtmpHandshake.HANDSHAKE_SIZE) {
				handshake.serverResponse2(buf.readBytes(RtmpHandshake.HANDSHAKE_SIZE));
				flags = 3;
				// after handshake, no need this handler. so remove from pipeline.
				ctx.pipeline().remove(this);
				ctx.fireChannelActive();
			}
			break;
		default:
			break;
		}
	}
}
