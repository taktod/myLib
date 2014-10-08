/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.SliceFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * SliceIDR (keyFrame?)
 * sliceIDRはIFrame(keyFrame)ならかならずsliceIDRになっているわけではなさそうです。
 * KeyFrameでもその前のフレームを参照するBFrameがあると、randomAccessできないので、randomAccessしてOKの場合のみ
 * sliceIDRになっているらしい。
 * 
 * TODO slice系のクラスは複数のnalで１つになっていることがあるので、複数のnalで構成されている場合は始めのnalに参照を持たせておいて
 * getPackBufferの動作で、一度に参照できるようにしておきたいところ。
 * こうしないとxuggleがエラーを吐く
 * @author taktod
 */
public class SliceIDR extends SliceFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SliceIDR.class);
	/** data */
	private ByteBuffer buffer = null;
	/**
	 * constructor
	 * @param forbiddenZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public SliceIDR(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
		super.setKeyFrame(true);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		setReadPosition(channel.position());
		setSize(channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - channel.position());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("data body is undefined");
		}
		setData(BufferUtil.connect(getTypeBuffer(),
				getSliceHeaderBuffer(),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		// make 00 00 00 01 sps 00 00 00 01 pps 00 00 00 01 sliceIdr
		BitConnector connector = new BitConnector();
		List<ByteBuffer> bufferList = new ArrayList<ByteBuffer>();
		// sps
		bufferList.add(connector.connect(new Bit32(1)));
		bufferList.add(getSps().getData());
		// pps
		bufferList.add(connector.connect(new Bit32(1)));
		bufferList.add(getPps().getData());
		// idrFrame
		bufferList.add(connector.connect(new Bit8()));
		for(H264Frame frame : getGroupFrameList()) {
			if(frame instanceof SliceIDR) {
				bufferList.add(connector.connect(new Bit24(1)));
				bufferList.add(frame.getData());
			}
			else {
				throw new Exception("unexpected frame is found.:" + getClass().getSimpleName());
			}
		}
		return BufferUtil.connect(bufferList);
	}
}
