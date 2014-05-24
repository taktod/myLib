/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.test;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.flazr.rtmp.RtmpDecoder;
import com.flazr.rtmp.client.ClientHandler;
import com.flazr.rtmp.client.ClientHandshakeHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.ttProject.flazr.rtmp.RtmpEncoderEx;

public class PublishTest {
	public static void main(String[] args) {
		final ClientOptions options = new ClientOptions();
		if(!options.parseCli(args)) {
			return;
		}
		if(options.getLoad() == 1 && options.getClientOptionsList() == null) {
			// 単一動作のみ
			// rtmpReaderを適当に作る必要あり。
			options.setFileToPublish(null);
			options.setReaderToPublish(new FlvTestReader());
			connect(options);
			return;
		}
		else {
			throw new RuntimeException("マルチ起動は許可しておりません。");
		}
	}
	private static void connect(final ClientOptions options) {
		final ClientBootstrap bootstrap = getBootstrap(Executors.newCachedThreadPool(), options);
		final ChannelFuture future = bootstrap.connect(new InetSocketAddress(options.getHost(), options.getPort()));
		future.awaitUninterruptibly();
		if(!future.isSuccess()) {
			
		}
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();
	}
	private static ClientBootstrap getBootstrap(final Executor executor, final ClientOptions options) {
		final ChannelFactory factory = new NioClientSocketChannelFactory(executor, executor);
		final ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("handshaker", new ClientHandshakeHandler(options));
				pipeline.addLast("decoder", new RtmpDecoder()); // amf3を使う理由もないので、通常のrtmpDecoderでOK
				pipeline.addLast("encoder", new RtmpEncoderEx());
				pipeline.addLast("handler", new ClientHandler(options));
				return pipeline;
			}
		});
		
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		return bootstrap;
	}
}
