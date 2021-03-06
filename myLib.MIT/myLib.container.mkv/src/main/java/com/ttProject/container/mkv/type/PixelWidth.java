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
 * PixelWidth
 * @author taktod
 */
public class PixelWidth extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public PixelWidth(EbmlValue size) {
		super(Type.PixelWidth, size);
	}
	/**
	 * constructor
	 */
	public PixelWidth() {
		this(new EbmlValue());
	}
}
