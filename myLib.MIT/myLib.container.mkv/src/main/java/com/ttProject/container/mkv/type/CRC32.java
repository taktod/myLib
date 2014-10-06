/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CRC32
 * @author taktod
 */
public class CRC32 extends MkvBinaryTag {
	/**
	 * constructor
	 * @param size
	 */
	public CRC32(EbmlValue size) {
		super(Type.CRC32, size);
	}
	/**
	 * constructor
	 */
	public CRC32() {
		this(new EbmlValue());
	}
}
