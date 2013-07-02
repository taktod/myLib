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
 * @author taktod
 */
public class ClientHandlerEx extends ClientHandler {
	/** メッセージ送信先のwriter */
	private RtmpWriter writer = null;
	/**
	 * コンストラクタ
	 * @param options
	 */
	public ClientHandlerEx(ClientOptions options) {
		super(options);
		writer = options.getWriterToSave();
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
