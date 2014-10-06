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
 * DisplayHeight
 * @author taktod
 */
public class DisplayHeight extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public DisplayHeight(EbmlValue size) {
		super(Type.DisplayHeight, size);
	}
	/**
	 * constructor
	 */
	public DisplayHeight() {
		this(new EbmlValue());
	}
}
