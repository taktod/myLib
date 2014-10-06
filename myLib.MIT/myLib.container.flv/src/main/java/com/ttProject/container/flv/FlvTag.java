/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv;

import java.nio.ByteBuffer;

import com.ttProject.container.Container;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * base of flvtag.
 * flvデータのタグ
 * @author taktod
 */
public abstract class FlvTag extends Container {
	private final Bit8  tagType; // 0x8 0x9 0x12 only?
	private       Bit24 dataSize     = new Bit24();
	private       Bit24 timestamp    = new Bit24();
	private       Bit8  timestampExt = new Bit8();
	private       Bit24 streamId     = new Bit24();
	private       Bit32 prevTagSize  = new Bit32();
	/**
	 * constructor
	 */
	public FlvTag(Bit8 tagType) {
		this.tagType = tagType;
		super.setTimebase(1000); // timebase must be 1/1000
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// first 11 byte will be read.
		// hold the position.
		super.setPosition(channel.position() - 1);
		BitLoader loader = new BitLoader(channel);
		loader.load(dataSize, timestamp, timestampExt, streamId);
		prevTagSize = new Bit32(dataSize.get() + 11);
		super.setPts(timestampExt.get() << 24 | timestamp.get());
		super.setSize(dataSize.get() + 11 + 4);
		super.update();
	}
	/**
	 * get first 11 bytes.(named startBuffer)
	 * @return
	 */
	protected ByteBuffer getStartBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(tagType, dataSize, timestamp, timestampExt, streamId);
	}
	/**
	 * get last 4 bytes.(named tailBuffer)
	 * @return
	 */
	protected ByteBuffer getTailBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(prevTagSize);
	}
	/**
	 * get the size data in tail buffer.
	 * (datasize - 4);
	 * @return
	 */
	protected int getPrevTagSize() {
		return prevTagSize.get();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData(ByteBuffer data) {
		dataSize.set(data.remaining() - 11 - 4);
		prevTagSize = new Bit32(dataSize.get() + 11);
		super.setData(data);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPts(long pts) {
		timestamp.set((int)(pts & 0x00FFFFFF));
		timestampExt.set((int)(pts >>> 24) & 0xFF);
		super.setPts(pts);
		super.update();
	}
	@Override
	protected void setSize(int size) {
		dataSize.set(size - 15);
		prevTagSize.set(size - 4);
		super.setSize(size);
		super.update();
	}
}
