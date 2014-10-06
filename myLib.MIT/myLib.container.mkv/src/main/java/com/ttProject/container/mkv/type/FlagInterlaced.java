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
 * FlagInterlaced
 * @author taktod
 */
public class FlagInterlaced extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public FlagInterlaced(EbmlValue size) {
		super(Type.FlagInterlaced, size);
	}
	/**
	 * constructor
	 */
	public FlagInterlaced() {
		this(new EbmlValue());
	}
}
