/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.opus.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.opus.OpusFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * コメントフレーム(oggにはあるっぽい。なくてもいいのかも？)
 * とりあえずoggにはあるけど、webmにはなかった。
 * OpusTags
 * venderLength:
 * venderData
 * elementNum
 * Data1Length
 * Data1Data
 * Data2Length
 * Data2Data
 * ....
 * webm(mkv) -> ogg変換したかったら適当にねつ造したほうがよさそう。
 * @author taktod
 */
public class CommentFrame extends OpusFrame {
	/** ロガー */
	private Logger logger = Logger.getLogger(CommentFrame.class);
	private String opusString = "OpusTags";
	private String venderName;
	private Integer elementSize = null;
	private List<String> elementList = new ArrayList<String>();
	private ByteBuffer tmpBuffer = null;
	/**
	 * コンストラクタ
	 */
	public CommentFrame() {
		super.update();
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		logger.info("ここで読み込みを実施する。");
		IReadChannel targetChannel = null;
		if(tmpBuffer != null) {
			tmpBuffer = BufferUtil.connect(
				tmpBuffer, BufferUtil.safeRead(channel, channel.size() - channel.position())
			);
			targetChannel = new ByteReadChannel(tmpBuffer);
			tmpBuffer = null;
		}
		else {
			targetChannel = channel;
		}
		if(venderName == null) {
			venderName = readString(targetChannel);
			if(venderName == null) {
				return;
			}
			logger.info("venderName:" + venderName);
		}
		if(elementSize == null) {
			Integer size = readInt(targetChannel);
			if(size == null) {
				return;
			}
			elementSize = size;
		}
		for(int i = 0;i < elementSize; i ++) {
			String element = readString(targetChannel);
			if(element == null) {
				return;
			}
			elementList.add(element);
		}
		logger.info(venderName);
		logger.info(elementList);
		super.update();
	}
	/**
	 * データを読み込もうとしてデータサイズが足りなかったらnullを返す
	 * @param channel
	 * @return
	 */
	private String readString(IReadChannel channel) throws Exception {
		Integer length = readInt(channel);
		if(length == null) {
			return null;
		}
		if(channel.size() - channel.position() < length) {
			tmpBuffer = ByteBuffer.allocate(channel.size() - channel.position() + 4);
			tmpBuffer.order(ByteOrder.LITTLE_ENDIAN);
			tmpBuffer.putInt(length);
			tmpBuffer.put(BufferUtil.safeRead(channel, channel.size() - channel.position()));
			tmpBuffer.flip();
			return null;
		}
		return new String(BufferUtil.safeRead(channel, length).array());
	}
	/**
	 * データを読み込もうとしてデータサイズが足りなかったらnullを返す
	 * @param channel
	 * @return
	 */
	private Integer readInt(IReadChannel channel) throws Exception {
		if(channel.size() - channel.position() < 4) {
			tmpBuffer = BufferUtil.safeRead(channel, channel.size() - channel.position());
			return null;
		}
		ByteBuffer buffer = BufferUtil.safeRead(channel, 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.getInt();
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(venderName == null) {
			venderName = "myLib.opus.muxer";
		}
		int size = 8 + 4 + venderName.length() + 4;
		for(String element : elementList) {
			size += 4 + element.length();
		}
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(opusString.getBytes());
		buffer.putInt(venderName.length());
		buffer.put(venderName.getBytes());
		buffer.putInt(elementList.size());
		for(String element : elementList) {
			buffer.putInt(element.length());
			buffer.put(element.getBytes());
		}
		buffer.flip();
		setSize(buffer.remaining());
		setData(buffer);
	}
	public void setVenderName(String name) {
		this.venderName = name;
		super.update();
	}
	public void addElement(String data) {
		elementList.add(data);
		elementSize = elementList.size();
		super.update();
	}
	public void removeElement(String data) {
		elementList.remove(data);
		elementSize = elementList.size();
		super.update();
	}
	@Override
	public boolean isComplete() {
		return venderName != null && elementSize != null && elementSize == elementList.size();
	}
}
