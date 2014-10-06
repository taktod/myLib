/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.util.BufferUtil;

/**
 * mkvStringTag
 * TODO now works with utf8. need to change?
 * @author taktod
 */
public abstract class MkvStringTag extends MkvTag{
	private String value = null;
	/**
	 * constructor
	 * @param id
	 * @param size
	 */
	public MkvStringTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		value = new String(BufferUtil.safeRead(channel, getMkvSize()).array()).intern();
		super.load(channel);
	}
	/**
	 * ref the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * set the value
	 * @param data
	 */
	public void setValue(String data) {
		value = data;
		getTagSize().set(value.getBytes().length);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(value == null) {
			throw new Exception("value is not defined.");
		}
		BitConnector connector = new BitConnector();
		ByteBuffer data = BufferUtil.connect(connector.connect(getTagId(), getTagSize()), ByteBuffer.wrap(value.getBytes()));
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
		data.append(" string:").append(value);
		return data.toString();
	}
}
