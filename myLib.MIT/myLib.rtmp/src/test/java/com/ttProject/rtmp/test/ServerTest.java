/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.test;

import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import org.apache.log4j.Logger;

import com.ttProject.util.HexUtil;

/**
 * netty4の動作を調べてみた。
 * @author taktod
 */
public class ServerTest {
	private Logger logger = Logger.getLogger(ServerTest.class);
	public void serverTest() throws Exception {
		logger.info("serverTest");
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap()
			.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast(new ServerDecoder())
					.addLast("test", new ServerHandler());
				}
			})
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			ChannelFuture f = bootstrap.bind(12345).sync();
			f.channel().closeFuture().sync();
		}
		finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	public static class ServerDecoder extends ByteToMessageDecoder {
		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf in,
				List<Object> out) throws Exception {
			System.out.println("decodeする");
			if(in.readableBytes() < 4) {
				return;
			}
			out.add(in.readBytes(4));
		}
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx)
				throws Exception {
			super.channelReadComplete(ctx);
		}
		@Override
		protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in,
				List<Object> out) throws Exception {
			// 読み込みおわったら次にまわす形にしてみるか？
			System.out.println("removeする");
			ctx.channel().pipeline().remove(this);
		}
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			// これを実行すると、中途でpipelineから外されてしまうみたいです。
//			ctx.pipeline().remove(this);
			super.channelActive(ctx);
		}
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			System.out.println("channelReadする");
			System.out.println(msg);
			super.channelRead(ctx, msg);
		}
	}
	public static class ServerHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx)
				throws Exception {
			System.out.println("readComplete.");
		}
		@Override
		public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
			// TODO Auto-generated method stub
			super.handlerAdded(ctx);
		}
		@Override
		public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
			super.handlerRemoved(ctx);
		}
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			System.out.println("channelRead is called.");
			ByteBuf buf = (ByteBuf)msg;

			System.out.println(HexUtil.toHex(buf.nioBuffer()));
			((ByteBuf) msg).release();
		}
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("channelActive");
		}
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("channelInactive");
		}
		@Override
		public void channelRegistered(ChannelHandlerContext ctx)
				throws Exception {
			System.out.println("channelRegistered");
		}
		@Override
		public void channelUnregistered(ChannelHandlerContext ctx)
				throws Exception {
			System.out.println("channelUnregistered");
		}
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
	}
}
