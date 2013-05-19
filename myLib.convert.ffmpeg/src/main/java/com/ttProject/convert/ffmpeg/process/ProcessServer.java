package com.ttProject.convert.ffmpeg.process;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 子プロセスにデータを送信するサーバー動作
 * @author taktod
 */
public class ProcessServer {
	/** ロガー */
	private Logger logger = LoggerFactory.getLogger(ProcessServer.class);
	/** つながっているクライアントのchannelデータ */
	private final Set<Channel> channels = new HashSet<Channel>();
	private final Channel serverChannel;
	private final ServerBootstrap bootstrap;
	private final Set<String> keySet = new HashSet<String>();
	/**
	 * コンストラクタ
	 * @param port
	 */
	public ProcessServer(int port) {
		bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("handler", new ProcessServerHandler());
				return pipeline;
			}
		});
		serverChannel = bootstrap.bind(new InetSocketAddress(port));
		System.out.println("サーバーを立ち上げました。");
	}
	/**
	 * 動作キーデータを登録する
	 * @param keySet
	 */
	public void addKey(String key) {
		this.keySet.add(key);
	}
	public Set<String> getKeySet() {
		return keySet;
	}
	/**
	 * データを送る
	 * @param buffer
	 */
	public void sendData(ChannelBuffer buffer) {
		synchronized(channels) {
			for(Channel channel : channels) {
				channel.write(buffer);
			}
		}
	}
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
	private class ProcessServerHandler extends SimpleChannelUpstreamHandler {
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
			synchronized(channels) {
				channels.add(e.getChannel());
			}
		}
		/**
		 * メッセージ取得動作
		 * @param ctx
		 * @param e
		 * @throws Exception
		 */
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			System.out.println("応答をうけとりました。");
			if(keySet.size() == 0) {
				// すでに全キー接続完了か確認する
				return;
			}
			Object message = e.getMessage();
			System.out.println(message.getClass());
			if(message instanceof ChannelBuffer) {
				System.out.println("channelBufferなので処理します。");
				ByteBuffer buffer = ((ChannelBuffer) message).toByteBuffer();
				byte[] data = new byte[buffer.remaining()];
				buffer.get(data);
				keySet.remove(new String(data).intern());
				if(keySet.size() == 0) {
					logger.info("子プロセスから全接続をうけとったので、処理開始");
					System.out.println("子プロセスから接続をうけとりました。処理するよん。");
					synchronized(keySet) {
						keySet.notifyAll(); // 通知を実行する
					}
				}
			}
		}
	}
}
