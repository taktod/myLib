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
 * FlagForced
 * @author taktod
 */
public class FlagForced extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public FlagForced(EbmlValue size) {
		super(Type.FlagForced, size);
	}
	/**
	 * constructor
	 */
	public FlagForced() {
		this(new EbmlValue());
	}
}
