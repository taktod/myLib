/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * signed int data.
 * TODO negative value doesn't work properly. I want to get the sample.
 * @author taktod
 */
public abstract class MkvSignedIntTag extends MkvTag {
	private BitN value;
	/**
	 * constructor
	 * @param id
	 * @param size
	 */
	public MkvSignedIntTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		List<Bit8> bit8List = new ArrayList<Bit8>();
		for(int i = 0;i < getMkvSize();i ++) {
			bit8List.add(new Bit8());
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(bit8List.toArray(new Bit8[]{}));
		value = new BitN(bit8List.toArray(new Bit8[]{}));
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
	/**
	 * ref value
	 * @return
	 */
	public int getValue() {
		return value.get();
	}
	/**
	 * set value
	 * @param data
	 * @throws Exception
	 */
	public void setValue(int data) {
		if(data >>> 8 == 0) {
			value = new BitN(new Bit8((int)data));
			getTagSize().set(1);
		}
		else if(data >>> 16 == 0) {
			value = new Bit16((int)data);
			getTagSize().set(2);
		}
		else if(data >>> 24 == 0) {
			value = new Bit24((int)data);
			getTagSize().set(3);
		}
		else {
			value = new Bit32((int)data);
			getTagSize().set(4);
		}
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		data.append(" int:").append(value.getLong());
		return data.toString();
	}
}
