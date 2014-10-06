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
 * BlockDuration
 * @author taktod
 */
public class BlockDuration extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public BlockDuration(EbmlValue size) {
		super(Type.BlockDuration, size);
	}
	/**
	 * constructor
	 */
	public BlockDuration() {
		this(new EbmlValue());
	}
}
