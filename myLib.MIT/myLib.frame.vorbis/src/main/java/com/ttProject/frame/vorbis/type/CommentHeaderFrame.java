/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit48;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * header frame for vorbis
 * packetType: 1byte 0x03 comment header
 * string: 6Byte "vorbis"
 * venderLength: 4byte integer
 * venderString: nbyte
 * [repeat]
 * iterateNum: 4byte integer
 * length: 4byte integer
 * string: nbyte (utf8?)
 * [repeat end]
 * framing flag 1bit(actually 1byte.)
 * 
 * @see http://www.xiph.org/vorbis/doc/Vorbis_I_spec.html#x1-620004.2.2
 * @author taktod
 */
public class CommentHeaderFrame extends VorbisFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(CommentHeaderFrame.class);
	private Bit8   packetType = new Bit8();
	private Bit48  string     = new Bit48();
	private String venderName = null;
	private Bit32  iterateNum = new Bit32();
	private List<String> elementList = new ArrayList<String>();
	private Bit1   lastFlag = new Bit1();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(packetType, string);
		if(packetType.get() != 3) {
			throw new Exception("unexpected packet type value.");
		}
		if(string.getLong() != 0x736962726F76L) {
			throw new Exception("string value is unexpected.");
		}
		Bit32 size = new Bit32();
		loader.load(size);
		venderName = new String(BufferUtil.safeRead(channel, size.get()).array());
		loader.load(iterateNum);
		for(int i = 0;i < iterateNum.get();i ++) {
			loader.load(size);
			String data = new String(BufferUtil.safeRead(channel, size.get()).array());
			elementList.add(data);
		}
		loader.load(lastFlag);
		if(lastFlag.get() != 1) {
			throw new Exception("end flag is unexpected.");
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(venderName == null) {
			venderName = "myLib.vorbis.muxer";
		}
		int size = 1 + 6 + 4 + venderName.length() + 4 + 1;
		for(String element : elementList) {
			size += 4 + element.length();
		}
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put((byte)0x03);
		buffer.put("vorbis".getBytes());
		buffer.putInt(venderName.length());
		buffer.put(venderName.getBytes());
		buffer.putInt(elementList.size());
		for(String element : elementList) {
			buffer.putInt(element.length());
			buffer.put(element.getBytes());
		}
		buffer.put((byte)0x01);
		buffer.flip();
		setSize(buffer.remaining());
		setData(buffer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
	@Override
	public int getSize() {
		try {
			getData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.getSize();
	}
	/**
	 * ref the minimum size buffer.
	 * @return
	 */
	public ByteBuffer getMinimumBuffer() {
		ByteBuffer buffer = ByteBuffer.allocate(35);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put((byte)0x03);
		buffer.put("vorbis".getBytes());
		String name = "myLib.vorbis.muxer";
		buffer.putInt(name.length());
		buffer.put(name.getBytes());
		buffer.putInt(0);
		buffer.put((byte)1);
		buffer.flip();
		return buffer;
	}
}
