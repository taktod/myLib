package com.ttProject.flazr.rtmp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpDecoder.DecoderState;
import com.flazr.rtmp.message.ChunkSize;
import com.flazr.rtmp.message.MessageType;
import com.ttProject.flazr.rtmp.message.MetadataAmf3;
import com.ttProject.util.HexUtils;

/**
 * Rtmpのメッセージのデコーダー処理
 * amf3のmetaデータに対応した動作にしてあります
 * @author taktod
 */
public class RtmpDecoderEx extends ReplayingDecoder<DecoderState> {
	/** ロガー */
	private static final Logger logger = LoggerFactory.getLogger(RtmpDecoderEx.class);
	/**
	 * コンストラクタ
	 */
	public RtmpDecoderEx() {
		super(DecoderState.GET_HEADER);
	}
	private RtmpHeader header;
	private int channelId;
	private ChannelBuffer payload;
	private int chunkSize = 128;

	private final RtmpHeader[] incompleteHeaders = new RtmpHeader[RtmpHeader.MAX_CHANNEL_ID];
	private final ChannelBuffer[] incompletePayloads = new ChannelBuffer[RtmpHeader.MAX_CHANNEL_ID];
	private final RtmpHeader[] completedHeaders = new RtmpHeader[RtmpHeader.MAX_CHANNEL_ID];

	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer in, DecoderState state) throws Exception {
		switch(state) {
		case GET_HEADER:
			header = new RtmpHeader(in, incompleteHeaders);
			channelId = header.getChannelId();
			if(incompletePayloads[channelId] == null) {
				incompleteHeaders[channelId] = header;
				incompletePayloads[channelId] = ChannelBuffers.buffer(header.getSize());
			}
			payload = incompletePayloads[channelId];
			checkpoint(DecoderState.GET_PAYLOAD);
		case GET_PAYLOAD:
			final byte[] bytes = new byte[Math.min(payload.writableBytes(), chunkSize)];
			in.readBytes(bytes);
			payload.writeBytes(bytes);
			checkpoint(DecoderState.GET_HEADER);
			// chunkがまだあるか確認する。
			if(payload.writable()) {
				return null;
			}
			// もうchunkがない
			incompletePayloads[channelId] = null;
			try {
				if(header.getMessageType() == MessageType.COMMAND_AMF3
//				|| header.getMessageType() == MessageType.METADATA_AMF3
				|| header.getMessageType() == MessageType.SHARED_OBJECT_AMF3) {
					logger.warn("解釈が設定されていない命令なので、無視しておきます。(このままだとプロセスが落ちます)");
					logger.info("type:{}, dump:{}", header.getMessageType(), HexUtils.toHex(bytes, true));
					return null;
				}
				final RtmpHeader prevHeader = completedHeaders[channelId];
				
				if(!header.isLarge()) {
					header.setTime(prevHeader.getTime() + header.getDeltaTime());
				}
				final RtmpMessage message = MessageTypeDecode(header, payload);
				if(header.isChunkSize()) {
					final ChunkSize csMessage = (ChunkSize) message;
					chunkSize = csMessage.getChunkSize();
				}
				completedHeaders[channelId] = header;
				return message;
			}
			catch (Exception e) {
				logger.error("decode error:", e);
				logger.info("------------------ type:{} -----------------", header.getMessageType());
				logger.info(HexUtils.toHex(bytes, true));
				return null;
			}
		default:
			throw new RuntimeException("unexpected decoder state:" + state);
		}
	}
	private RtmpMessage MessageTypeDecode(RtmpHeader header, ChannelBuffer payload) {
		// metadataAmf3みたいな独自実装はMessageType.decodeで解釈できないので、ここで分岐させておく。
		if(header.getMessageType() == MessageType.METADATA_AMF3) {
			return new MetadataAmf3(header, payload);
		}
		else {
			return MessageType.decode(header, payload);
		}
	}
}