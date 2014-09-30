/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.rtmp.message;

import java.util.List;

import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.amf.Amf0Object;
import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.CommandAmf0;
import com.ttProject.container.flv.amf.Amf0Value;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * AMF3のコマンド動作
 * 
 * @author taktod
 */
public class CommandAmf3 implements RtmpMessage {
	/** ロガー */
	private static final Logger logger = LoggerFactory.getLogger(CommandAmf3.class);
	/** 動作ヘッダ */
	private final RtmpHeader header;
	private String name;
	private Integer transactionId;
	private Object object;
	private Object[] args;
	/**
	 * コンストラクタ
	 * @param header
	 * @param in
	 */
	public CommandAmf3(RtmpHeader header, ChannelBuffer in) {
		this.header = header;
		decode(in);
	}
	@Override
	public void decode(ChannelBuffer in) {
		// ここをつくる必要あり。
		// とりあえず、IReadChannelで処理したいので、そうする。
		int length = in.readableBytes();
		byte[] bytes = new byte[length];
		in.readBytes(bytes);
		try {
			IReadChannel channel = new ByteReadChannel(bytes);
			// まず1byte目を確認する。
			switch(BufferUtil.safeRead(channel, 1).get()) {
			case 0x00:
				// 1byte目が0x00ならAMF0として処理する。
				name = (String)Amf0Value.getValueObject(channel);
				transactionId = ((Double)Amf0Value.getValueObject(channel)).intValue();
				object = Amf0Value.getValueObject(channel);
				List<Object> list = new ArrayList<Object>();
				while(channel.size() > channel.position()) {
					list.add(Amf0Value.getValueObject(channel));
				}
				args = list.toArray();
				break;
			case 0x11:
				// 1byte目が0x11ならAMF3として処理する。(たぶん)
				throw new Exception("中身はAMF0であることを期待しておきます。");
			}
		}
		catch(Exception e) {
			logger.error("failed to parse data.", e);
		}
	}
	@Override
	public ChannelBuffer encode() {
		// AMF3としてサーバーに命令を転送する動作はいまのところ実装しないでおきます。
		throw new RuntimeException("encode is not supported now.");
	}
	@Override
	public RtmpHeader getHeader() {
		return header;
	}
	public String getName() {
		return name;
	}
	public Integer getTransactionId() {
		return transactionId;
	}
	public Object getObject() {
		return object;
	}
	public Object getArg(int index) {
		return args[index];
	}
	public int getArgCount() {
		if(args == null) {
			return 0;
		}
		return args.length;
	}
	public CommandAmf0 transform() {
		CommandAmf0 command0 = new CommandAmf0(transactionId, name, (Amf0Object)object, args);
		return command0;
	}
}
