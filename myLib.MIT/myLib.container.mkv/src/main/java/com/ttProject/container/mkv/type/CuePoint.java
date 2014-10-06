/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CuePoint
 * @author taktod
 */
public class CuePoint extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public CuePoint(EbmlValue size) {
		super(Type.CuePoint, size);
	}
	/**
	 * constructor
	 */
	public CuePoint() {
		this(new EbmlValue());
	}
}
