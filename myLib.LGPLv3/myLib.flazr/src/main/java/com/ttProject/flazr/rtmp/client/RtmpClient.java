/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.rtmp.client;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.client.ClientHandler;
import com.flazr.rtmp.client.ClientHandshakeHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.util.Utils;
import com.ttProject.flazr.client.ClientOptionsEx;
import com.ttProject.flazr.rtmp.RtmpDecoderEx;
import com.ttProject.flazr.rtmp.RtmpEncoderEx;

/**
 * copy of original rtmpClient(from flazr)
 * @author taktod
 */
public class RtmpClient {
	private static final Logger logger = LoggerFactory.getLogger(RtmpClient.class);
	public static void main(String[] args) {
		final ClientOptions options = new ClientOptionsEx();
		if(!options.parseCli(args)) {
			return;
		}
		Utils.printlnCopyrightNotice();
		final int count = options.getLoad();
		if(count == 1 && options.getClientOptionsList() == null) {
			connect(options);
			return;
		}
		//======================================================================
		final Executor executor = Executors.newFixedThreadPool(options.getThreads());
		if(options.getClientOptionsList() != null) {
			logger.info("file driven load testing mode, lines: {}", options.getClientOptionsList().size());
			int line = 0;
			for(final ClientOptions tempOptions : options.getClientOptionsList()) {
				line++;
				logger.info("running line #{}", line);
				for(int i = 0; i < tempOptions.getLoad(); i++) {
					final int index = i + 1;
					final int tempLine = line;
					executor.execute(new Runnable() {
						@Override public void run() {
							final ClientBootstrap bootstrap = getBootstrap(executor, tempOptions);
							bootstrap.connect(new InetSocketAddress(tempOptions.getHost(), tempOptions.getPort()));
							logger.info("line #{}, spawned connection #{}", tempLine + 1, index);
						}
					});
				}
			}
			return;
		}
		//======================================================================
		final ClientBootstrap bootstrap = getBootstrap(executor, options);
		logger.info("load testing mode, no. of connections to create: {}", count);
		options.setSaveAs(null);
		for(int i = 0; i < count; i++) {
			final int index = i + 1;
			executor.execute(new Runnable() {
				@Override public void run() {
					bootstrap.connect(new InetSocketAddress(options.getHost(), options.getPort()));
					logger.info("spawned connection #{}", index);
				}
			});
		}
		// TODO graceful shutdown
	}

	public static void connect(final ClientOptions options) {  
		final ClientBootstrap bootstrap = getBootstrap(Executors.newCachedThreadPool(), options);
		final ChannelFuture future = bootstrap.connect(new InetSocketAddress(options.getHost(), options.getPort()));
		future.awaitUninterruptibly();
		if(!future.isSuccess()) {
			// future.getCause().printStackTrace();
			logger.error("error creating client connection: {}", future.getCause().getMessage());
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
				final ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("handshaker", new ClientHandshakeHandler(options));
				pipeline.addLast("decoder", new RtmpDecoderEx());
				pipeline.addLast("encoder", new RtmpEncoderEx());
				pipeline.addLast("handler", new ClientHandler(options));
				return pipeline;
			}
		});
		bootstrap.setOption("tcpNoDelay" , true);
		bootstrap.setOption("keepAlive", true);
		return bootstrap;
	}
}
