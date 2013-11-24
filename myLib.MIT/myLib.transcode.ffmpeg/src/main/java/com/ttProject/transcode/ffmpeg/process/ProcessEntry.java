package com.ttProject.transcode.ffmpeg.process;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 * 中間に挟むjava process
 * @author taktod
 * 動作引数としては、port番号とキー情報をおくります。
 * 起動したら。port番号のサーバーに接続を実施し、コネクトした時に自分のキー情報をサーバーに送ります。
 * サーバーからデータをうけとったら標準出力としてデータを吐かせます。
 * 
 * log4jをつかったらややこしくなるので、とりあえず標準エラーにデータを出力しておきます。
 */
public class ProcessEntry {
	/** 標準出力用の出力チャンネル */
	private WritableByteChannel stdout;
	/** 動作キー */
	private final String key;
	/**
	 * 動作エントリー
	 * @param args
	 */
	public static void main(String[] args) {
		System.err.println("動作を開始します。");
		if(args == null || args.length != 2) {
			System.err.println("引数の数がおかしいです");
			return;
		}
		// ポート番号
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		}
		catch (Exception e) {
			System.err.println("入力ポート番号が数値解釈できませんでした。");
		}
		// アクセスキー
		String key = args[1];
		new ProcessEntry(port, key);
	}
	/**
	 * コンストラクタ
	 * @param port
	 * @param key
	 */
	public ProcessEntry(int port, String key) {
		this.key = key;
		// 標準出力用のチャンネルを開いておく。
		stdout = java.nio.channels.Channels.newChannel(System.out);
		// clientとしてサーバーつなげる動作をつくっておく。
		ClientBootstrap bootstrap = new ClientBootstrap(
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
		
		System.err.println("コネクト開始します。");
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(port));
		future.awaitUninterruptibly();
		if(future.isSuccess()) {
			future.getChannel().getCloseFuture().awaitUninterruptibly();
		}
		bootstrap.releaseExternalResources();
	}
	/**
	 * 内部のクライアントクラス
	 * @author taktod
	 */
	private class ProcessClientHandler extends SimpleChannelUpstreamHandler {
		/** 初めのキーを送ったかどうかフラグ */
		boolean passedFirstReply = false;
		/**
		 * メッセージをうけとったときの動作
		 */
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			// メッセージの受け取りがしばらくなくなったらしぬ動作がいるかも・・・
			ByteBuffer buffer = ((ChannelBuffer)e.getMessage()).toByteBuffer();
			if(!passedFirstReply) {
				try {
					byte[] reply = buffer.duplicate().array();
					if('r' == reply[0]
					&& 'e' == reply[1]
					&& 'f' == reply[2]
					&& 'u' == reply[3]
					&& 's' == reply[4]
					&& 'e' == reply[5]
					&& 'd' == reply[6]) {
						System.err.println("拒否メッセージがきたので、とめておきます。");
						System.exit(0);
					}
					passedFirstReply = true;
				}
				catch (Exception ee) {
					ee.printStackTrace();
				}
			}
			stdout.write(buffer);
		}
		/**
		 * 接続したときのやりとり。
		 */
		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception {
			System.err.println("接続したのでキー情報を応答します。");
			ChannelBuffer keyBuffer = ChannelBuffers.buffer(key.length());
			keyBuffer.writeBytes(key.getBytes());
			e.getChannel().write(keyBuffer);
		}
	}
}
