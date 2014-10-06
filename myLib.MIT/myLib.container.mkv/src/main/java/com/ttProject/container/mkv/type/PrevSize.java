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
 * PrevSize
 * @author taktod
 */
public class PrevSize extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public PrevSize(EbmlValue size) {
		super(Type.PrevSize, size);
	}
	/**
	 * constructor
	 */
	public PrevSize() {
		this(new EbmlValue());
	}
}
