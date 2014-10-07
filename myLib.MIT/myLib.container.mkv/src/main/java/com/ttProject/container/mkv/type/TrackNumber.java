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
 * TrackNumber
 * @author taktod
 */
public class TrackNumber extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public TrackNumber(EbmlValue size) {
		super(Type.TrackNumber, size);
	}
	/**
	 * constructor
	 */
	public TrackNumber() {
		this(new EbmlValue());
	}
}
