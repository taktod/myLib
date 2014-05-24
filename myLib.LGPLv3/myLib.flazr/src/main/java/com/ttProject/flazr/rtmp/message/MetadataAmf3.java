/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.rtmp.message;

import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.MetadataAmf0;
import com.ttProject.container.flv.amf.Amf0Value;
import com.ttProject.container.flv.amf.Amf3Value;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

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
	/** 保持データmap */
	private Map<String, Object> data = null;
	/** 設定名称(onMetaData固定) */
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
	/**
	 * channelBufferからデータを復元します
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void decode(ChannelBuffer in) {
		int length = in.readableBytes();
		byte[] bytes = new byte[length];
		in.readBytes(bytes);
		try {
			// 処理しやすいようにするため、IReadChannelの形に変化させます。
			IReadChannel channel = new ByteReadChannel(bytes);
			
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
			if(BufferUtil.safeRead(channel, 1).get() != 0x11) { // 0x00になっていて、AMF0の内容を保持している可能性もあるかも・・・
				throw new Exception("中途のデータはAMF3のObjectデータとして転送されていることを期待しておきます。");
			}
			// 内部データを解析しておく。
			data = (Map<String, Object>)Amf3Value.getValueObject(channel);
			// まだデータがある場合は読み込んで次のObjectに配置した方がいいのかもしれない。
		}
		catch (Exception e) {
			logger.error("MetadataAmf3を解析しているときに、例外が発生しました。", e);
			logger.error("errorData: {}", HexUtil.toHex(bytes, true));
			throw new RuntimeException("解析不能な例外が発生しました。");
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChannelBuffer encode() {
		// AMF3として、メタデータを転送する用事がないので、サポートしません。
		throw new RuntimeException("encode is not supported now.");
	}
	/**
	 * header部取得
	 */
	@Override
	public RtmpHeader getHeader() {
		return header;
	}
	/**
	 * 名称取得(onMetaData)
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 内部データ取得
	 * @return
	 */
	public Map<String, Object> getData() {
		return data;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder("MetadataAmf3");
		data.append(" name:").append(getName());
		data.append(" data:").append(getData());
		return data.toString();
	}
	public MetadataAmf0 transform() {
		MetadataAmf0 metadata0 = new MetadataAmf0(name, data);
		return metadata0;
	}
}
