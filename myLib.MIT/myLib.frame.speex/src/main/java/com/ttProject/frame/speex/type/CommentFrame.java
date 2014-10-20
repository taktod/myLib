/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * comment Frame for speex.
 * kind of metadata.
 * 
 * 4byte int venderLength
 * nbyte string venderName
 * 4byte int elementNum
 *  4byte elementLength
 *  nbyte elementString
 * repeat as much as element has.
 * @author taktod
 * TODO こちらの動作ですが、解析途上の場合は、フラグで確認できるようにして、再度loadし直したら続きから処理できるようにしたいところ。
 */
public class CommentFrame extends SpeexFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(CommentFrame.class);
	/** venderName */
	private String venderName = null;
	/** element size */
	private Integer elementSize = null;
	/** element list */
	private List<String> elementList = new ArrayList<String>();
	/** working buffer */
	private ByteBuffer tmpBuffer = null;
	/**
	 * constructor
	 */
	public CommentFrame() {
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
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
		}
		if(elementSize == null) {
			Integer size = readInt(targetChannel);
			if(size == null) {
				return;
			}
			elementSize = size;
		}
		for(int i = 0;i < elementSize;i ++) {
			String element = readString(targetChannel);
			if(element == null) {
				return;
			}
			elementList.add(element);
		}
		super.update();
	}
	/**
	 * try to read data.
	 * @param channel
	 * @return if need more data, return null.
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
	 * try to read int.
	 * @param channel
	 * @return if need more data, return null.
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
	public int getSize() {
		try {
			getData();
		}
		catch(Exception e) {
		}
		return super.getSize();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(venderName == null) {
			venderName = "myLib.speex.muxer";
		}
		// venderLength
		// venderData
		// elementListSize
		//  elementDataLength
		//  elementData
		// ...
		int size = 4 + venderName.length() + 4;
		for(String element : elementList) {
			size += 4 + element.length();
		}
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
	public void setVenderName(String name) {
		this.venderName = name;
		super.update();
	}
	public void addElement(String data) {
		elementList.add(data);
		super.update();
	}
	public void removeElement(String data) {
		elementList.remove(data);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComplete() {
		return venderName != null && elementSize != null && elementSize == elementList.size();
	}
}
