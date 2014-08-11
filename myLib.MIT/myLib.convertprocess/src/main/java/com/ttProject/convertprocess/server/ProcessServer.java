/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.ttProject.util.BufferUtil;

/**
 * 標準入力としてデータをffmpeg等の外部プロセスに渡すために利用するサーバー動作
 * @author taktod
 */
public class ProcessServer {
	/** ロガー */
	private Logger logger = Logger.getLogger(ProcessServer.class);
	/** つながっているクライアントのchannelデータ */
	private final Set<Channel> channels = new HashSet<Channel>();
	/** 動作サーバーチャンネル */
	private final Channel serverChannel;
	/** サーバー用の動作bootstrap */
	private final ServerBootstrap bootstrap;
	/**
	 * コンストラクタ
	 * @param port
	 */
	public ProcessServer(int port) {
		ExecutorService executor = Executors.newCachedThreadPool();
		bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(executor, executor));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("handler", new ProcessServerHandler());
				return pipeline;
			}
		});
		serverChannel = bootstrap.bind(new InetSocketAddress(port));
	}
	/**
	 * データを送る
	 * @param buffer
	 */
	public void sendData(ByteBuffer buffer) {
		// 先頭にsize情報をつけておかないとclient側でどこまでが一まとまりかがわからなくなります。
		ByteBuffer size = ByteBuffer.allocate(4);
		size.putInt(buffer.remaining());
		size.flip();
		ChannelBuffer sendBuffer = ChannelBuffers.copiedBuffer(BufferUtil.connect(size, buffer));
		// データのサイズを先行して設定しないとだめです。
		synchronized(channels) {
			for(Channel channel : channels) {
				channel.write(sendBuffer); // futureをつかって待ってもだめっぽいね、別threadで転送を実行しておこうと思う。
			}
		}
	}
	/**
	 * サーバーを閉じます
	 */
	public void closeServer() {
		synchronized(channels) {
			for(Channel channel : channels) {
				channel.close();
			}
			channels.clear();
		}
		ChannelFuture future = serverChannel.close();
		future.awaitUninterruptibly();
		bootstrap.releaseExternalResources();
	}
	/**
	 * 処理クラス
	 * @author taktod
	 */
	@ChannelPipelineCoverage("one")
	private class ProcessServerHandler extends SimpleChannelUpstreamHandler {
		/**
		 * 例外取得時
		 */
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
				throws Exception {
//			super.exceptionCaught(ctx, e);
		}
		/**
		 * 接続したときの動作
		 * channelオブジェクトを保持しておく
		 * @param ctx
		 * @param e
		 * @throws Exception
		 */
		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception {
			logger.info("接続してきた。");
			synchronized (channels) {
				channels.add(e.getChannel());
			}
		}
/*		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			synchronized (channels) {
				channels.add(e.getChannel());
			}
		}*/
		/**
		 * 終了時
		 */
		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {
			logger.info("閉じました");
			synchronized (channels) {
				channels.remove(e.getChannel());
			}
		}
		/**
		 * 切断時
		 */
		@Override
		public void channelDisconnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception {
			logger.info("切断しました。");
			synchronized (channels) {
				channels.remove(e.getChannel());
			}
		}
	}
}
