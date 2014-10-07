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
 * TrackTimecodeScale
 * this tag is deprected.
 * @author taktod
 */
public class TrackTimecodeScale extends MkvFloatTag {
	/**
	 * constructor
	 * @param size
	 */
	public TrackTimecodeScale(EbmlValue size) {
		super(Type.TrackTimecodeScale, size);
	}
	/**
	 * constructor
	 */
	public TrackTimecodeScale() {
		this(new EbmlValue());
	}
}
