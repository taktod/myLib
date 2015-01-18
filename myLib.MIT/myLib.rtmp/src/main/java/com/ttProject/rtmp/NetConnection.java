/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.amf.Amf0Object;
import com.ttProject.rtmp.message.IRtmpMessage;
import com.ttProject.rtmp.message.type.Amf0Command;
import com.ttProject.rtmp.netty.ClientHandler;
import com.ttProject.rtmp.netty.HandshakeHandler;
import com.ttProject.rtmp.netty.NetConnectionHandler;
import com.ttProject.rtmp.netty.RtmpDecoder;
import com.ttProject.rtmp.netty.RtmpEncoder;

/**
 * NetConnection
 * @author taktod
 */
public class NetConnection {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(NetConnection.class);
	private String host;
	private int    port;
	private String app;
	private NetConnectionHandler handler = null;
	private INetStatusEventListener listener = null;
	private ChannelFuture future = null;
	private EventLoopGroup workerGroup;
	private int transactionId = 2;
	private int bufferLength = 0;
	public void connect(String address) {
		// parse rtmp address information.
		Pattern pattern = Pattern.compile("rtmp://([^/:]+)(:[0-9]+)?/(.+)");
		Matcher matcher = pattern.matcher(address);
		if(matcher.matches()) {
			if(matcher.groupCount() != 3) {
				throw new RuntimeException("matcher count is invalid.");
			}
			host = matcher.group(1);
			if(matcher.group(2) == null) {
				port = 1935;
			}
			else {
				port = Integer.parseInt(matcher.group(2).substring(1));
			}
			app = matcher.group(3);
		}
		else {
			throw new RuntimeException("failed to analyze rtmp address.");
		}
		String tcUrl = "rtmp://" + host + ":" + port + "/" + app;
		handler = new NetConnectionHandler(tcUrl, this);
		// try to connect.
		workerGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap()
		.group(workerGroup)
		.channel(NioSocketChannel.class)
		.option(ChannelOption.SO_KEEPALIVE, true)
		.option(ChannelOption.TCP_NODELAY, true)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline()
				.addLast(new HandshakeHandler())
				.addLast(new RtmpDecoder())
				.addLast(new RtmpEncoder())
				.addLast(new ClientHandler())
				.addLast(handler);
			}
		});
		future = bootstrap.connect(host, port);
	}
	public void setObjectEncoding(int objectEncoding) {
		if(objectEncoding != 0) {
			throw new RuntimeException("now amf0 is only supported.");
		}
	}
	public void setListener(INetStatusEventListener listener) {
		this.listener = listener;
	}
	public int getBufferLength() {
		return bufferLength;
	}
	public void addLast(ChannelHandler handler) {
		future.channel().pipeline().addLast(handler);
	}
	public int writeAndFlush(IRtmpMessage message) {
		int resId = 0;
		if(message instanceof Amf0Command) {
			Amf0Command command = (Amf0Command)message;
			command.setTransactionId(transactionId);
			resId = transactionId;
			transactionId ++;
		}
		future.channel().writeAndFlush(message);
		return resId;
	}
	public void close() {
		future.channel().close();
		workerGroup.shutdownGracefully();
	}
	public void closeForWait() throws Exception {
		try {
			future.channel().closeFuture().sync();
		}
		finally {
			workerGroup.shutdownGracefully();
		}
	}
	public void onStatusEvent(Amf0Object<String, Object> obj) {
		if(listener != null) {
			listener.onStatusEvent(obj);
		}
	}
}
