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
 * CueTime
 * @author taktod
 */
public class CueTime extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public CueTime(EbmlValue size) {
		super(Type.CueTime, size);
	}
	/**
	 * constructor
	 */
	public CueTime() {
		this(new EbmlValue());
	}
}
