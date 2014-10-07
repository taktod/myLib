/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Timecode
 * @author taktod
 */
public class Timecode extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public Timecode(EbmlValue size) {
		super(Type.Timecode, size);
	}
	/**
	 * constructor
	 */
	public Timecode() {
		this(new EbmlValue());
	}
}
