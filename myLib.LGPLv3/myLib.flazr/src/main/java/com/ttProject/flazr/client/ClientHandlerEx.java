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
 * this is the extend class for clientHandler to deal with metadataAmf3.
 * however, this class is no more needed,
 * because rtmpDecoderEx will reply metadataAmf0 instead of metadataAmf3 and commandAmf0 instead of commandAmf3.
 * @author taktod
 */
@Deprecated
public class ClientHandlerEx extends ClientHandler {
	/** hold options */
	private final ClientOptions options;
	/**
	 * constructor
	 * @param options
	 */
	public ClientHandlerEx(ClientOptions options) {
		super(options);
		this.options = options;
	}
	/**
	 * in the case of message received
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
		final RtmpMessage message = (RtmpMessage)event.getMessage();
		MessageType type = message.getHeader().getMessageType();
		switch(type) {
		case METADATA_AMF3:
			MetadataAmf3 metadata = (MetadataAmf3) message;
			if(metadata.getName().equals("onMetaData")) {
				// ref the option always, because rtmpWriter can be settled by other code.
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
