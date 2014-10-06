/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SeekHead
 * @author taktod
 */
public class SeekHead extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public SeekHead(EbmlValue size) {
		super(Type.SeekHead, size);
	}
	/**
	 * constructor
	 */
	public SeekHead() {
		this(new EbmlValue());
	}
	/**
	 * constructor
	 * @param position
	 */
	public SeekHead(long position) {
		this();
		setPosition((int)position);
	}
	/**
	 * set position
	 * @param position
	 */
	public void setPosition(long position) {
		super.setPosition((int)position);
	}
}
