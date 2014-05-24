/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp8.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.vp8.Vp8Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit19;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.util.BufferUtil;

public class IntraFrame extends Vp8Frame {
	private ByteBuffer buffer;
	public IntraFrame(Bit1 frameType, Bit3 version, Bit1 showFrame, Bit19 firstPartSize) {
		super(frameType, version, showFrame, firstPartSize);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setReadPosition(channel.position());
		setSize(channel.size());
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("本体データが設定されていません");
		}
		setData(BufferUtil.connect(getHeaderBuffer(),
				buffer));
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
}
