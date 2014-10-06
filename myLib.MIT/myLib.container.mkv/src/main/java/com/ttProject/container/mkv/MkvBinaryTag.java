/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.util.BufferUtil;

/**
 * mkvBinaryTag
 * @author taktod
 */
public abstract class MkvBinaryTag extends MkvTag {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvBinaryTag.class);
	private ByteBuffer buffer = null;
	/**
	 * constructor
	 * @param id
	 * @param size
	 */
	public MkvBinaryTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, getRemainedSize());
		super.load(channel);
	}
	/**
	 * ref the data size to read on load func.
	 * @return
	 */
	protected int getRemainedSize() {
		return getMkvSize();
	}
	/**
	 * ref the binary data.
	 * @return
	 */
	public ByteBuffer getMkvData() {
		return buffer.duplicate();
	}
	/**
	 * set the binary data.
	 * @param data
	 */
	public void setValue(ByteBuffer data) {
		buffer = data.duplicate();
		getTagSize().set(buffer.remaining());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("buffer data is undefined.");
		}
		BitConnector connector = new BitConnector();
		ByteBuffer data = BufferUtil.connect(
				connector.connect(getTagId(), getTagSize()),
				buffer
		);
		setSize(data.remaining());
		super.setData(data);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		if(buffer == null) {
			data.append(" binary:").append("null");
		}
		else {
			data.append(" binary:").append(Integer.toHexString(buffer.remaining()));
		}
		return data.toString();
	}
}
