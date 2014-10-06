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
 * FlagLacing
 * @author taktod
 */
public class FlagLacing extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public FlagLacing(EbmlValue size) {
		super(Type.FlagLacing, size);
	}
	/**
	 * constructor
	 */
	public FlagLacing() {
		this(new EbmlValue());
	}
}
