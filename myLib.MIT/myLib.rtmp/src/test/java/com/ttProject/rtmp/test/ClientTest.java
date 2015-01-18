/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ClientTest {
	private Logger logger = Logger.getLogger(ClientTest.class);
	@Test
	public void test() throws Exception {
		logger.info("logger");
		connect();
	}
	public void connect() throws Exception {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap()
			.group(workerGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast(new ClientEncoder())
					.addLast(new ClientHandler());
				}
			});
			ChannelFuture f = bootstrap.connect("localhost", 12345).sync();
			System.out.println("connected");
			f.channel().closeFuture().sync();
			System.out.println("synced.");
		}
		finally {
			workerGroup.shutdownGracefully();
		}
	}
	public static class ClientEncoder extends ChannelOutboundHandlerAdapter {
		@Override
		public void read(ChannelHandlerContext ctx) throws Exception {
			System.out.println("read");
			super.read(ctx);
		}
		@Override
		public void write(ChannelHandlerContext ctx, Object msg,
				ChannelPromise promise) throws Exception {
			String str = (String)msg;
			final ByteBuf time = ctx.alloc().buffer(str.length());
			time.writeBytes(str.getBytes());
			System.out.println("msg:" + msg);
			System.out.println("write");
			super.write(ctx, time, promise);
		}
	}
	public static class ClientHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelActive(final ChannelHandlerContext ctx) throws Exception {
			System.out.println("active");
			final ChannelFuture f = ctx.writeAndFlush("ttttaa");
			System.out.println("sendMessage");
			f.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture arg0) throws Exception {
					System.out.println("done");
					ctx.close();
				}
			});
		}
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("inactive");
			super.channelInactive(ctx);
		}
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			System.out.println("read");
			super.channelRead(ctx, msg);
		}
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx)
				throws Exception {
			System.out.println("readComplete");
			super.channelReadComplete(ctx);
		}
		@Override
		public void channelRegistered(ChannelHandlerContext ctx)
				throws Exception {
			System.out.println("registered");
			super.channelRegistered(ctx);
		}
		@Override
		public void channelUnregistered(ChannelHandlerContext ctx)
				throws Exception {
			System.out.println("unregistered");
			super.channelUnregistered(ctx);
		}
	}
}
