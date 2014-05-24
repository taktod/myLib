/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpWriter;
import com.flazr.rtmp.client.ClientHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.MessageType;
import com.ttProject.flazr.rtmp.message.MetadataAmf3;

/**
 * metadataAmf3を理解させるために、ClientHandlerの拡張をつくりました。
 * このクラスはもう必要ないと思われます。
 * AMF3のクラスが同様の内容を持つAMF0のクラスを応答するようになったため
 * @author taktod
 */
@Deprecated
public class ClientHandlerEx extends ClientHandler {
	/** 動作オプション保持 */
	private final ClientOptions options;
	/**
	 * コンストラクタ
	 * @param options
	 */
	public ClientHandlerEx(ClientOptions options) {
		super(options);
		this.options = options;
	}
	/**
	 * メッセージの受信処理
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
		final RtmpMessage message = (RtmpMessage)event.getMessage();
		MessageType type = message.getHeader().getMessageType();
		switch(type) {
		case METADATA_AMF3:
			MetadataAmf3 metadata = (MetadataAmf3) message;
			if(metadata.getName().equals("onMetaData")) {
				// 毎回参照するようにしておく。(データがかわっていっているため)
				RtmpWriter writer = options.getWriterToSave();
				if(writer != null) {
					writer.write(message);
				}
			}
			return;
		default:
			break;
		}
		super.messageReceived(ctx, event);
	}
}
