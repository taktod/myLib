/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.test.publish;

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

import com.flazr.rtmp.RtmpEncoder;
import com.flazr.rtmp.client.ClientHandshakeHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.ttProject.flazr.rtmp.RtmpDecoderEx;

/**
 * rtmpとして、他のサーバーにデータを投げる動作テスト
 * Aからデータを取得して、Bに方針するみたいな動作がしたい。
 * 
 * とりあえず次のようにしたい。
 * ・プロセスが起動されると、Aのサーバーの特定のアプリケーションを観察する。
 * ・映像をうけとったら・・・接続する・・・にしようか・・・
 * その方がただしい動作っぽいし。
 * ・publishNotifyがきたら接続してpublishする。(はじめから放送されている場合はonPublishはこないです。その場合はデータ転送があるかで判定すればいいか？)
 * ・unpublishNotifyがきたら切断してしまう。
 * ってのが一番いいかな。
 * よってclientHandlerExの拡張が必要。(publish unpublishを検知するため)
 * 
 * ・転送データはきちんとしたflvにする必要があるけど・・・
 * @author taktod
 */
public class RtmpPublishTest {
	private static final Logger logger = LoggerFactory.getLogger(RtmpPublishTest.class);
	public static void main(String[] args) {
		ClientOptions options = new ClientOptions();
		if(!options.parseCli(args)) {
			return;
		}
		if(options.getLoad() == 1 && options.getClientOptionsList() == null) {
			options.setWriterToSave(new ReceiveWriter());
			connect(options);
			return;
		}
		logger.error("シングルのみ許可しています。");
	}
	public static void connect(ClientOptions options) {
		ClientBootstrap bootstrap = getBootstrap(Executors.newCachedThreadPool(), options);
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(options.getHost(), options.getPort()));
		future.awaitUninterruptibly();
		if(!future.isSuccess()) {
			logger.error("client接続をつくるのに失敗した。");
		}
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();
	}
	private static ClientBootstrap getBootstrap(Executor executor, final ClientOptions options) {
		ChannelFactory factory = new NioClientSocketChannelFactory(executor, executor);
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("handshaker", new ClientHandshakeHandler(options));
				pipeline.addLast("decoder", new RtmpDecoderEx());
				pipeline.addLast("encoder", new RtmpEncoder());
				pipeline.addLast("handler", new ReceiveClientHandler(options));
				return pipeline;
			}
		});
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		return bootstrap;
	}
}
