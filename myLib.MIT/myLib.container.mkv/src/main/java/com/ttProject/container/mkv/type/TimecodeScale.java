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
 * TimecodeScale
 * this seems to be timescale for entire file.
 * there is another which named TrackTimecodeScale.(deprecated)
 * @author taktod
 */
public class TimecodeScale extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public TimecodeScale(EbmlValue size) {
		super(Type.TimecodeScale, size);
	}
	/**
	 * constructor
	 */
	public TimecodeScale() {
		this(new EbmlValue());
	}
	/**
	 * ref the data of timebase.
	 * @return
	 */
	public long getTimebaseValue() {
		return 1000000000L / getValue();
	}
}
