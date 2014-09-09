/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.server;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.ttProject.convertprocess.frame.IShareFrameListener;

/**
 * 動作させるプロセスのクライアント
 * @author taktod
 */
public class ProcessClient {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(ProcessClient.class);
	/** データ転送の結果を受け取るlistener */
	@SuppressWarnings("unused")
	private Set<IShareFrameListener> listeners = new HashSet<IShareFrameListener>(); // これ複数とる必要あるのかな？
	/** 接続bootstrap */
	private ClientBootstrap bootstrap;
	/** 接続状況用future */
	private ChannelFuture future = null;
	/**
	 * コンストラクタ
	 */
	public ProcessClient(final IShareFrameListener listener) {
		ExecutorService executor = Executors.newCachedThreadPool();
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(executor, executor));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("handler", new ProcessClientHandler(listener));
				return pipeline;
			}
		});
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
	}
	/**
	 * 接続を実施する
	 */
	public boolean connect(String server, int port) {
		future = bootstrap.connect(new InetSocketAddress(server, port));
		future.awaitUninterruptibly();
		return future.isSuccess();
	}
	/**
	 * 処理がおわるまで待機します。
	 */
	public void waitForClose() {
		// 処理が終わるまで待機
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		// リソースを解放しておく。
		bootstrap.releaseExternalResources();
	}
	/**
	 * 閉じます
	 */
	public void close() {
		// その場でcloseします。
		future.getChannel().close();
		// リソースを解放しておく。
		bootstrap.releaseExternalResources();
	}
}
