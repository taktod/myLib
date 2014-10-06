/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SeekID
 * @author taktod
 */
public class SeekID extends MkvBinaryTag {
	private Type targetTag = null;
	/**
	 * constructor
	 * @param size
	 */
	public SeekID(EbmlValue size) {
		super(Type.SeekID, size);
	}
	/**
	 * constructor
	 */
	public SeekID() {
		this(new EbmlValue());
	}
	/**
	 * ref the ebmlId for the seek target.
	 * @return
	 * @throws Exception
	 */
	public Type getId() throws Exception {
		if(targetTag == null) {
			IReadChannel channel = null;
			channel = new ByteReadChannel(getMkvData());
			BitLoader loader = new BitLoader(channel);
			EbmlValue value = new EbmlValue();
			loader.load(value);
			targetTag = Type.getType(value.getEbmlValue());
		}
		return targetTag;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		try {
			data.append(" seekId:").append(getId());
		}
		catch(Exception e) {
			data.append(" seekId: unknown.");
		}
		return data.toString();
	}
}
