/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;


import com.ttProject.container.mkv.MkvDateTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * DateUTC
 * @author taktod
 */
public class DateUTC extends MkvDateTag {
	/**
	 * constructor
	 * @param size
	 */
	public DateUTC(EbmlValue size) {
		super(Type.DateUTC, size);
	}
	/**
	 * constructor
	 */
	public DateUTC() {
		this(new EbmlValue());
	}
}
