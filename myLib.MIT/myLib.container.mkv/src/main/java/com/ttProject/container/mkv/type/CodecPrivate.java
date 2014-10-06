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
 * CodecPrivate
 * @author taktod
 */
public class CodecPrivate extends MkvBinaryTag {
	/**
	 * constructor
	 * @param size
	 */
	public CodecPrivate(EbmlValue size) {
		super(Type.CodecPrivate, size);
	}
	/**
	 * constructor
	 */
	public CodecPrivate() {
		this(new EbmlValue());
	}
}
