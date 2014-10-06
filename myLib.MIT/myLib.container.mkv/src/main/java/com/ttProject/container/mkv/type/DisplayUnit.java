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
 * DisplayUnit
 * @author taktod
 */
public class DisplayUnit extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public DisplayUnit(EbmlValue size) {
		super(Type.DisplayUnit, size);
	}
	/**
	 * constructor
	 */
	public DisplayUnit() {
		this(new EbmlValue());
	}
}
