/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.nio.ByteBuffer;
import java.util.Date;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit64;

/**
 * mkvDateTag
 * this is nano sec from 01/01/2001
 * @author taktod
 */
public abstract class MkvDateTag extends MkvTag {
	private Bit64 value;
	/**
	 * constructor
	 * @param id
	 * @param size
	 */
	public MkvDateTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		switch(getMkvSize()) {
		case 8:
			value = new Bit64();
			break;
		default:
			throw new Exception("unexpected mkvSize:" + getMkvSize());
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(value);
		super.load(channel);
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(value == null) {
			throw new Exception("value is undefined");
		}
		BitConnector connector = new BitConnector();
		ByteBuffer data = connector.connect(getTagId(), getTagSize(), value);
		setSize(data.remaining());
		super.setData(data);
	}
	public Date getValue() {
		return new Date(946684800000L + value.getLong() / 1000000);
	}
	public void setValue(Date date) {
		value = new Bit64();
		value.setLong((date.getTime() - 946684800000L) * 1000000L);
		getTagSize().set(8);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		data.append(" date:").append(getValue());
		return data.toString();
	}
}
