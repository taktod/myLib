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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.io.flv.FlvAtom;
import com.flazr.rtmp.RtmpDecoder;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpReader;
import com.flazr.rtmp.client.ClientHandshakeHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.Metadata;
import com.flazr.rtmp.message.MetadataAmf0;
import com.ttProject.container.flv.FlvCodecType;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.flazr.rtmp.RtmpEncoderEx;
import com.ttProject.flazr.unit.TagManager;

/**
 * rtmpの送信を実行する動作
 * 受け取る側がwriterなので、送り出す側がreader(flvファイルデータを読み込んで流すみたいな感じですね。きっと)
 * @author taktod
 */
public class SendReader implements RtmpReader {
	/** ロガー */
	private final Logger logger = LoggerFactory.getLogger(SendReader.class);
	/** 配信用のthreadに渡すデータの中継役 */
	private final LinkedBlockingQueue<FlvAtom> dataQueue = new LinkedBlockingQueue<FlvAtom>();
	/** デフォルトのメタデータ */
	private Metadata metadata = new MetadataAmf0("onMetaData");
	/** 集合メッセージの設定保持(常に0を期待します) */
	private int aggregateDuration = 0;
	
	/** 接続サーバーアドレス */
	private final String rtmpAddress;
	/** アドレス解析用(正規表現) */
	private static final Pattern pattern = Pattern.compile("^rtmp://([^/:]+)(:[0-9]+)?/(.*)(.*?)$");
	
	/** 接続処理用 */
	private ClientBootstrap bootstrap = null;
	private ChannelFuture future = null;
	private ClientOptions options;
	private SendClientHandler clientHandler = null;
	/** 接続中管理フラグ */
	private boolean isWorking = true;
	/** 放送中管理フラグ */
	private boolean isPublishing = true;
	
	/** flvの特別なデータ管理用 */
	private AudioTag audioMshTag = null;
	private VideoTag videoMshTag = null;
	/** dataQueue上の処理位置 */
//	private int processPos = -1;
	/** 現在の処理位置 */
//	private int savePos = 0;
	
	/** FlvTag -> flvAtomの変換補助 */
	private final TagManager manager = new TagManager();

	/**
	 * コンストラクタ
	 * @param rtmpAddress 接続先アドレス
	 */
	public SendReader(String rtmpAddress) {
		this.rtmpAddress = rtmpAddress;
	}
	/**
	 * 接続を開きます。
	 * @throws Exception
	 */
	public void open() throws Exception {
		options = new ClientOptions();
		parseAddress(options);
		options.publishLive();
		options.setFileToPublish(null);
		options.setReaderToPublish(this);
		options.setStreamName("test");
		logger.info("open connection");
		
		// 接続を開始します。blockするので、threadにやらせます。
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(isWorking) {
					try {
						dataQueue.clear();
						audioMshTag = null;
						videoMshTag = null;
//						processPos = -1;
//						savePos = 0;
						// このタイミングで前のmshデータを要求してやる必要があるっぽいです。
					}
					catch(Exception e) {
					}
					connect(options);
					logger.info("connect success.");
				}
				logger.info("thread of rtmpPublish is ended.");
				// なにか停止処理をいれる場合はここにいれるべし。
			}
		});
		// プロセスが落ちたら自動的におわりたいので、daemon化しておきます。
		t.setDaemon(true);
		t.start();
	}
	/**
	 * 接続処理
	 * @param options
	 */
	private void connect(final ClientOptions options) {
		bootstrap = getBootstrap(Executors.newCachedThreadPool(), options);
		future = bootstrap.connect(new InetSocketAddress(options.getHost(), options.getPort()));
		future.awaitUninterruptibly();
		if(!future.isSuccess()) {
			logger.warn("failed to connect");
		}
		else {
			logger.info("success to connect");
		}
		// これやっちゃうと・・・他の処理がしにそうな気がするけど・・・
		future.getChannel().getCloseFuture().awaitUninterruptibly(); 
		bootstrap.getFactory().releaseExternalResources();
	}
	/**
	 * bootstrapの作成処理
	 * @param executor
	 * @param options
	 * @return
	 */
	private ClientBootstrap getBootstrap(final Executor executor, final ClientOptions options) {
		final ChannelFactory factory = new NioClientSocketChannelFactory(executor, executor);
		final ClientBootstrap bootstrap = new ClientBootstrap(factory);
		clientHandler = new SendClientHandler(options);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("handshaker", new ClientHandshakeHandler(options));
				pipeline.addLast("decoder", new RtmpDecoder());
				pipeline.addLast("encoder", new RtmpEncoderEx());
				// 通常のclientHandlerを利用すると、接続だけして、publishする前という動作ができない。
				pipeline.addLast("handler", clientHandler);
				return pipeline;
			}
		});
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		return bootstrap;
	}
	/**
	 * アドレスの解析処理
	 * @param options
	 * @throws Exception
	 */
	private void parseAddress(ClientOptions options) throws Exception {
		Matcher matcher = pattern.matcher(rtmpAddress);
		if(!matcher.matches()) {
			throw new Exception("rtmp address is invalid");
		}
		if(matcher.groupCount() != 4) {
			throw new Exception("failed to rtmpAddress parse.");
		}
		options.setHost(matcher.group(1));
		if(matcher.group(2) == null) {
			options.setPort(1935);
		}
		else {
			options.setPort(Integer.parseInt(matcher.group(2).substring(1)));
		}
		options.setAppName(matcher.group(3));
	}
	/**
	 * 放送開始を実行(接続と放送開始が別になっているのが元なので、ここにあります。)
	 */
	public void publish() {
		// 送信stream名を変更かける場合はここで処理する必要があります。
		// stream名が一定なら、別にここでやる必要はないです。
//		options.setStreamName();
		isPublishing = true; // 放送中フラグをONにします。
		clientHandler.publish(); // clientHandlerにpublishの実行をさせます。
	}
	/**
	 * 放送を中断
	 * この処理は内部でclientBootstrapを止めてしまうので、再publishをするには再コネクトが必要になります。
	 */
	public void unpublish() {
		isPublishing = false;
		clientHandler.unpublish();
	}
	/**
	 * 外から動作を停止させる場合の処理
	 * こっちは明示的にthreadを終了させてしまいます。
	 */
	public void stop() {
		isWorking = false;
		future.getChannel().close();
	}

	/**
	 * 停止処理
	 * 停止したときに呼ばれてやること。
	 * いまのところ特になし。
	 */
	@Override
	public void close() {
	}
	/**
	 * metaデータの応答
	 */
	@Override
	public Metadata getMetadata() {
		return metadata;
	}
	/**
	 * 配信開始時に一番始めに送信するメッセージ設定
	 * ここに特殊なコマンドをいれて認証したりしてもいい。
	 */
	@Override
	public RtmpMessage[] getStartMessages() {
		return new RtmpMessage[]{metadata};
	}
	/**
	 * 再生位置の取得
	 * 今回つくりたいのはliveデータの転送なので、再生位置のコントロールは基本しません。
	 */
	@Override
	@Deprecated
	public long getTimePosition() {
		throw new RuntimeException("seek is not supported for live.");
	}
	/**
	 * シーク動作
	 * 禁止します(liveなので)
	 */
	@Override
	public long seek(long timePosition) {
		throw new RuntimeException("seek is not supported for live.");
	}
	/**
	 * 次のメッセージがくるかどうか
	 * liveなので、もっていなくてもあとから来ると信じます
	 * よってtrue固定
	 */
	@Override
	public boolean hasNext() {
		return true;
	}
	/**
	 * 次のメッセージ要求されたときの動作
	 * nullを応答するとunpublishするようになっているみたいです。
	 */
	@Override
	public RtmpMessage next() {
		if(aggregateDuration <= 0) {
			if(!isPublishing || !isWorking) {
				return null; // 動作していなければnullを応答
			}
			try {
				FlvAtom atom = dataQueue.take();
				logger.info("send flvAtom to server.");
				return atom;
			}
			catch(Exception e) {
				logger.error("", e);
				return null;
			}
		}
		else {
			throw new RuntimeException("chunk for aggregate is not supported.");
		}
	}
	/**
	 * 集合メッセージ用のデーア設置処理(0がくることを想定しています。)
	 */
	@Override
	public void setAggregateDuration(int targetDuration) {
		aggregateDuration = targetDuration;
	}
	/**
	 * rtmpに流すflvTagを受け入れます
	 * @param flvTag
	 * @throws Exception
	 */
	public void send(FlvTag flvTag) throws Exception {
		// mshは保持しておく。
		// publish中じゃなかったら送らない。
		// 開始位置がまだ決定していない場合は、timestampを保持しておいて、そこから経過時間とする。
		if(flvTag == null) {
			return;
		}
		// 前からのずれは任意で処置しておく
		if(flvTag instanceof VideoTag) {
			// 映像タグ
			VideoTag vTag = (VideoTag)flvTag;
			logger.info("videoTag:{}", vTag);
			if(vTag.isSequenceHeader()) {
				videoMshTag = vTag;
				return;
			}
			else if(vTag.getCodec() != FlvCodecType.H264) {
				videoMshTag = null;
			}
			if(vTag.getCodec() == FlvCodecType.H264 && vTag.isKeyFrame() &&videoMshTag != null) {
				// mshも送っておく。
				videoMshTag.setPts(vTag.getPts());
				dataQueue.add(manager.getAtom(videoMshTag));
			}
			dataQueue.add(manager.getAtom(flvTag));
		}
		else if(flvTag instanceof AudioTag) {
			// 音声タグ
			AudioTag aTag = (AudioTag)flvTag;
			logger.info("audioTag:{}", aTag);
			if(aTag.isSequenceHeader()) {
				audioMshTag = aTag;
				return;
			}
			else if(aTag.getCodec() != FlvCodecType.AAC) {
				audioMshTag = null;
			}
			if(aTag.getCodec() == FlvCodecType.AAC && audioMshTag != null) {
				audioMshTag.setPts(aTag.getPts());
				dataQueue.add(manager.getAtom(audioMshTag));
			}
			dataQueue.add(manager.getAtom(flvTag));
		}
		// 始めの転送では、mshを忘れずにおくるようにしておく。
		// データをqueueにためておく。
		//  以上でいいはず。
	}
}
