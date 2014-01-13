package com.ttProject.flazr.test.publish;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.client.ClientHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.MessageType;

/**
 * メッセージ受け側のclientHandlerの動作定義
 * @author taktod
 */
public class ReceiveClientHandler extends ClientHandler {
	/** ロガー */
	private Logger logger = LoggerFactory.getLogger(ReceiveClientHandler.class);
	private final ClientOptions options;
	/**
	 * コンストラクタ
	 * @param options
	 */
	public ReceiveClientHandler(ClientOptions options) {
		super(options);
		this.options = options;
	}
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
		final RtmpMessage message = (RtmpMessage)event.getMessage();
		MessageType type = message.getHeader().getMessageType();
		switch(type) {
		case COMMAND_AMF0:
		case COMMAND_AMF3:
			Command command = (Command)message;
			String name = command.getName();
			if("onStatus".equals(name)) {
				ReceiveWriter receiveWriter = (ReceiveWriter)options.getWriterToSave();
				@SuppressWarnings("unchecked")
				final Map<String, Object> temp = (Map<String, Object>) command.getArg(0);
				final String code = (String)temp.get("code");
				if("NetStream.Play.UnpublishNotify".equals(code)) {
					logger.info("unpublishされた");
					// 放送しているデータがある場合は停止させる。
					receiveWriter.publishStop();
				}
				else if("NetStream.Play.PublishNotify".equals(code)) {
					logger.info("publishされた");
					// 放送開始してなければ開始しないとだめ。
					receiveWriter.publishStart();
				}
				else if("NetStream.Play.Start".equals(code)) {
					logger.info("playStartされた");
					// とりあえずpublishスタートしておいて損はない
					receiveWriter.playStart();;
				}
			}
			break;
		default:
			break;
		}
		// 必要があればデータをhookしておく。
		// とりあえず、転送元サーバーのpublish unpublishは検知しておきたい。
		super.messageReceived(ctx, event);
	}
}
