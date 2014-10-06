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
 * FlagEnabled
 * @author taktod
 */
public class FlagEnabled extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public FlagEnabled(EbmlValue size) {
		super(Type.FlagEnabled, size);
	}
	/**
	 * constructor
	 */
	public FlagEnabled() {
		this(new EbmlValue());
	}
}
