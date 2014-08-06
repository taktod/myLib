/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.h264.H264Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.util.BufferUtil;

/**
 * FilterData
 * どうやらh264でbitrateを一定にするために埋めるデータっぽいです。
 * @author taktod
 */
public class FilterData extends H264Frame {
	/** データ本体 */
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param forbiddenZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public FilterData(Bit1 forbiddenZeroBit,
			Bit2 nalRefIdc,
			Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setReadPosition(channel.position());
		setSize(channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("データ実体が読み込まれていません");
		}
		setData(BufferUtil.connect(getTypeBuffer(),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
}
