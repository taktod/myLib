/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.server;

import java.net.InetSocketAddress;
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
import com.ttProject.frame.IFrame;

/**
 * データを受信して、標準出力として、プロセスにデータを渡すprocessのエントリーポイント
 * 実際はflv用、mkv用等・・・いろいろと派生をつくることになる予定
 * @author taktod
 */
public class ProcessEntry {
	/** 動作ロガー */
	private static Logger logger = Logger.getLogger(ProcessEntry.class);
	private ClientBootstrap bootstrap;
	private int port;
	/**
	 * メインエントリー
	 * @param args
	 */
	public static void main(String args[]) {
		if(args == null || args.length != 1) { // キーがなくなったので、１つになっているのが問題っぽい。
			for(String data : args) {
				logger.info(data);
			}
			logger.warn("引数の数がおかしいです。");
			System.exit(-1);
			return;
		}
		// ポート番号を指定して、アクセスしなければいけない。
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		}
		catch(Exception e) {
			System.err.println("入力ポート番号の数値解釈できませんでした。");
			System.exit(-1);
			return;
		}
		ProcessEntry entry = new ProcessEntry(port);
		entry.start();
	}
	/**
	 * コンストラクタ
	 * @param port
	 */
	public ProcessEntry(int port) {
		this.port = port;
		ExecutorService executor = Executors.newCachedThreadPool();
		bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(executor, executor));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("handler", new ProcessClientHandler(new IShareFrameListener() {
					@Override
					public void pushFrame(IFrame frame, int id) {
						// データをpushしたときの動作
					}
				}));
				return pipeline;
			}
		});
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
	}
	/**
	 * クライアントアクセス開始
	 */
	public void start() {
		ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", port));
		future.awaitUninterruptibly();
		if(future.isSuccess()) {
			future.getChannel().getCloseFuture().awaitUninterruptibly();
		}
		bootstrap.releaseExternalResources();
	}
}
