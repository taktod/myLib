/*
 * myLib - https:
 * //github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.ttProject.convertprocess.frame.ShareFrameData;
import com.ttProject.util.BufferUtil;

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
		logger.info("とりあえず出力");
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
		logger.info("処理おわり。");
	}
	/**
	 * コンストラクタ
	 * @param port
	 */
	public ProcessEntry(int port) {
		this.port = port;
//		ExecutorService executor = ;
		bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("handler", new ProcessClientHandler());
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
		logger.info("ここからコネクトを開始すればよい");
		ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", port));
		future.awaitUninterruptibly();
		logger.info("接続できた？");
		if(future.isSuccess()) {
			logger.info("接続成功した");
			future.getChannel().getCloseFuture().awaitUninterruptibly();
		}
		logger.info("終わりまできた。");
		bootstrap.releaseExternalResources();
	}
	/**
	 * データを受け取ったときの動作
	 * @author taktod
	 */
	@ChannelPipelineCoverage("one")
	private class ProcessClientHandler extends SimpleChannelUpstreamHandler {
		private int size = -1;
		private ByteBuffer buffer = null;
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
				throws Exception {
			logger.error("error", e.getCause());
		}
		@Override
		public synchronized void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			// メッセージをうけとったときに処理やっとく。
			if(buffer != null) {
				logger.info("メッセージうけとった。:" + buffer.remaining());
			}
			buffer = BufferUtil.connect(buffer, ((ChannelBuffer)e.getMessage()).toByteBuffer());
			while(buffer.remaining() > 0) {
				if(size == -1) {
					// はじめのデータサイズがはいっている。
					if(buffer.remaining() < 4) {
						return; // データが足りてない
					}
					size = buffer.getInt();
					logger.info("次のサイズ:" + size);
				}
				if(buffer.remaining() < size) {
					return; // データが足りてないその２
				}
				if(size < 0) {
					// データがおかしい。
					return;
				}
				ByteBuffer data = ByteBuffer.allocate(size);
				byte[] tmp = new byte[size];
				buffer.get(tmp);
				data.put(tmp);
				data.flip();
				// このdataからShareFrameDataを復元したいところ。
				try {
					getFrame(data);
				}
				catch(Exception ex) {
					logger.error("フレームの複製時に例外が発生しました。", ex);
				}
				// 次のデータ待ち
				logger.info("1フレーム処理おわり");
				size = -1;
			}
		}
		/**
		 * フレームに戻す
		 * @param data
		 */
		private void getFrame(ByteBuffer data) throws Exception {
			ShareFrameData shareFrameData = new ShareFrameData(data);
			logger.info(shareFrameData.getCodecType());
		}
	}
}
