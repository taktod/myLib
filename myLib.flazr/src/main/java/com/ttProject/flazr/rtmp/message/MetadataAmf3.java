package com.ttProject.flazr.rtmp.message;

import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.ttProject.media.flv.amf.Amf0Value;
import com.ttProject.media.flv.amf.Amf3Value;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtils;

/**
 * FlazrがMetadataAmf3に対応していないので、対応してみた。
 * onMetaData, Data, Data...という形になっているのが常っぽいので、この実装は、初めのDataのみしか対象にしていないので、まずいかもしれない。
 * @author taktod
 */
public class MetadataAmf3 implements RtmpMessage {
	/** ロガー */
	private static final Logger logger = LoggerFactory.getLogger(MetadataAmf3.class);
	/** rtmpHeader */
	private final RtmpHeader header;
	private Map<String, Object> data = null;
	private String name;
	/**
	 * コンストラクタ
	 * @param header
	 * @param in
	 */
	public MetadataAmf3(RtmpHeader header, ChannelBuffer in) {
		this.header = header;
		decode(in);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void decode(ChannelBuffer in) {
		int length = in.readableBytes();
		byte[] bytes = new byte[length];
		in.readBytes(bytes);
		try {
			// 処理しやすいようにするため、IFileReadChannelの形に変化させます。
			IFileReadChannel channel = new ByteReadChannel(bytes);
			
			// 先頭のデータを確認して、どういうデータであるか確認する。
			// 00 02 00 0A 6F 6E 4D 65 74 61 44 61 74 61であることを期待してあります。
			if(BufferUtil.safeRead(channel, 1).get() != 0x00) {
				throw new Exception("先頭のデータはAMF0として転送されてきていることが期待されています。");
			}
			// AMF0としてデータを読み込む
			String amf0Data = (String)Amf0Value.getValueObject(channel);
			if(!"onMetaData".equals(amf0Data)) {
				throw new Exception("metadataの指定文字列が取得できませんでした。");
			}
			name = amf0Data;
			// 11 Objectデータになっているはず
			if(BufferUtil.safeRead(channel, 1).get() != 0x11) {
				throw new Exception("中途のデータはAMF3のObjectデータとして転送されていることを期待しておきます。");
			}
			// 内部データを解析しておく。
			data = (Map<String, Object>)Amf3Value.getValueObject(channel);
			// まだデータがある場合は読み込んで次のObjectに配置した方がいいのかもしれない。
		}
		catch (Exception e) {
			logger.error("MetadataAmf3を解析しているときに、例外が発生しました。", e);
			logger.error("errorData: {}", HexUtils.toHex(bytes, true));
			throw new RuntimeException("解析不能な例外が発生しました。");
		}
	}
	@Override
	public ChannelBuffer encode() {
		// データの作成はあとで気が向いたらつくっておく。
		throw new RuntimeException("encode is not supported now.");
	}
	@Override
	public RtmpHeader getHeader() {
		return header;
	}
	public String getName() {
		return name;
	}
	public Map<String, Object> getData() {
		return data;
	}
}
