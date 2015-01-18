/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.netty;

import java.nio.ByteBuffer;

import com.ttProject.rtmp.header.HeaderType;
import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.IRtmpMessage;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * RtmpEncoder
 * @author taktod
 * make ByteBuf data from IRtmpMessage.
 */
public class RtmpEncoder extends ChannelOutboundHandlerAdapter {
	private int chunkSize = 128;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		if(msg instanceof IRtmpMessage) {
			IRtmpMessage message = (IRtmpMessage)msg;
			IRtmpHeader header = message.getHeader();
			ByteBuffer body = message.getData();
			header.setSize(body.remaining());
			
			boolean isFirst = true;
			do {
				int size = (body.remaining() > chunkSize ? chunkSize : body.remaining());
				byte[] data = new byte[size];
				body.get(data);
				if(isFirst) {
					ctx.write(Unpooled.wrappedBuffer(header.getData()));
					isFirst = false;
				}
				else {
					ctx.write(Unpooled.wrappedBuffer(header.switchTo(HeaderType.Type3).getData()));
				}
				ctx.write(Unpooled.wrappedBuffer(data));
			}while(body.remaining() > 0);
		}
	}
}
