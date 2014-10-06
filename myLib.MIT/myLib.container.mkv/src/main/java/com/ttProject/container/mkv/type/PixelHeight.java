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
 * PixelHeight
 * @author taktod
 */
public class PixelHeight extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public PixelHeight(EbmlValue size) {
		super(Type.PixelHeight, size);
	}
	/**
	 * constructor
	 */
	public PixelHeight() {
		this(new EbmlValue());
	}
}
