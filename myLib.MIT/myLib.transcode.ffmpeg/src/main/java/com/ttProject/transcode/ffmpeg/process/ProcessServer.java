package com.ttProject.transcode.ffmpeg.process;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
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

/**
 * 子プロセスにデータを送信するサーバー動作
 * @author taktod
 */
public class ProcessServer {
	/** ロガー */
	private final Logger logger = Logger.getLogger(ProcessServer.class);
	/** つながっているクライアントのchannelデータ */
	private final Set<Channel> channels = new HashSet<Channel>();
	/** 動作サーバーチャンネル */
	private final Channel serverChannel;
	/** サーバーbootstrap */
	private final ServerBootstrap bootstrap;
	/** アクセスがくる子プロセスのキー情報 */
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
	}
	/**
	 * 動作キーデータを登録する
	 * @param keySet
	 */
	public void addKey(String key) {
		this.keySet.add(key);
	}
	/**
	 * キーセットを参照します。
	 * @return
	 */
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
	/**
	 * サーバーを閉じる
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
			Object message = e.getMessage();
			if(message instanceof ChannelBuffer) {
				ByteBuffer buffer = ((ChannelBuffer) message).toByteBuffer();
				byte[] data = new byte[buffer.remaining()];
				buffer.get(data);
				if(!keySet.remove(new String(data).intern())) {
					// キーの設定のないアクセスだったのでおかしな接続であると判定します。
					logger.info("キーが合わない接続がきたので、拒否しておきます。");
//					channels.remove(e.getChannel());
					e.getChannel().write(ChannelBuffers.copiedBuffer("refused".getBytes()));
					return;
				}
				synchronized(channels) {
					channels.add(e.getChannel());
				}
				if(keySet.size() == 0) {
					logger.info("子プロセスから全接続をうけとったので、処理開始");
					synchronized(keySet) {
						keySet.notifyAll(); // 通知を実行する
					}
				}
			}
		}
	}
}
