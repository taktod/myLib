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
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;

/**
 * mkvFloatTag
 * can have double, too.
 * @author taktod
 */
public abstract class MkvFloatTag extends MkvTag {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvFloatTag.class);
	private BitN value;
	/**
	 * constructor
	 * @param id
	 * @param size
	 */
	public MkvFloatTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		switch(getMkvSize()) {
		case 4:
			value = new Bit32();
			break;
		case 8:
			value = new Bit64();
			break;
		default:
			throw new Exception("unexpected mkv size.");
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(value);
		super.load(channel);
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(value == null) {
			throw new Exception("value is not defined.");
		}
		BitConnector connector = new BitConnector();
		ByteBuffer data = connector.connect(getTagId(), getTagSize(), value);
		setSize(data.remaining());
		super.setData(data);
	}
	public double getValue() {
		if(value instanceof Bit32) {
			return Float.intBitsToFloat(value.get());
		}
		else {
			return Double.longBitsToDouble(value.getLong());
		}
	}
	public void setValue(float data) {
		value = new Bit32(Float.floatToIntBits(data));
		getTagSize().set(4);
		super.update();
	}
	public void setValue(double data) {
		value = new Bit64();
		value.setLong(Double.doubleToLongBits(data));
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
		data.append(" float:").append(getValue());
		return data.toString();
	}
}
