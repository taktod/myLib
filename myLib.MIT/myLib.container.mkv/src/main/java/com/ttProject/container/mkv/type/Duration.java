/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvFloatTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Duration
 * @author taktod
 */
public class Duration extends MkvFloatTag {
	/**
	 * constructor
	 * @param size
	 */
	public Duration(EbmlValue size) {
		super(Type.Duration, size);
	}
	/**
	 * constructor
	 */
	public Duration() {
		this(new EbmlValue());
	}
}
