/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.opus.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.opus.OpusFrame;
import com.ttProject.frame.opus.inner.type.CompresedFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.util.BufferUtil;

/**
 * opus frame
 * @author taktod
 * とりあえずサンプルの無音(48000Hz)がFC FF FEになったどういうことかな？
 */
public class Frame extends OpusFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	/** frameBuffer */
	private ByteBuffer frameBuffer = null; // フレームの中身がさらに内部フレームで細かくわかれることがあるっぽい。
//	private byte firstByte; // firstByteを取っている理由は、frameの切り分けで参照してしまうため。
	private Bit5 TOCconfig = new Bit5();
	/* 番号         モード   Band FrameSize(ms)
	 * 00 01 02 03 SILKのみ NB  10 20 40 60
	 * 04 05 06 07         MB  10 20 40 60
	 * 08 09 0A 0B         WB  10 20 40 60
	 * 0C 0D       Hybrid  SWB 10 20
	 * 0E 0F               FB  10 20
	 * 10 11 12 13 CELTのみ NB  2.5 5 10 20
	 * 14 15 16 17         MB  2.5 5 10 20
	 * 18 19 1A 1B         WB  2.5 5 10 20
	 * 1C 1D 1E 1F         SWB 2.5 5 10 20
	 */
	private Bit1 TOCs = new Bit1(); // 0:モノラル 1:ステレオ
	private Bit2 TOCc = new Bit2(); // パケットのフレーム数指定 0:1frame 1:2frame 同じ圧縮サイズ 2:2frame 違う圧縮サイズ 3:適当
	/**
	 * constructor
	 * @param firstByte
	 */
	public Frame(byte firstByte) {
		TOCconfig.set((firstByte & 0xF8) >> 3);
		TOCs.set((firstByte & 0x04) >> 2);
		TOCc.set(firstByte & 0x03);
	}
	/**
	 * constructor
	 */
	public Frame() {
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.setSize(channel.size());
		ByteBuffer buffer = ByteBuffer.allocate(channel.size() - 1);
		buffer.put(BufferUtil.safeRead(channel, channel.size() - 1));
		buffer.flip();
		frameBuffer = buffer;
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameBuffer is not loaded yet.");
		}
		BitConnector connector = new BitConnector();
		super.setData(BufferUtil.connect(
				connector.connect(TOCconfig, TOCs, TOCc),
				frameBuffer
		));
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
//		return getData();
		throw new Exception("packBuffer for opus is unknown.");
	}
	/**
	 * ref the MUframe list.
	 * @return
	 */
	public List<CompresedFrame> getMUFrameList() {
		List<CompresedFrame> result = new ArrayList<CompresedFrame>();
		switch(TOCc.get()) {
		case 0:
			result.add(new CompresedFrame(frameBuffer.duplicate()));
			break;
		case 1:
			
//			result.add();
//			break;
		case 2:
		case 3:
			throw new RuntimeException("undefined TOCc div definition.");
		}
		return result;
	}
	@Override
	public boolean isComplete() {
		return true;
	}
}
