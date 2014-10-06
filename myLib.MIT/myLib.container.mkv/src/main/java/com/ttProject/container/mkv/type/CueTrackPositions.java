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
 * CueTrackPositions
 * @author taktod
 */
public class CueTrackPositions extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public CueTrackPositions(EbmlValue size) {
		super(Type.CueTrackPositions, size);
	}
	/**
	 * constructor
	 */
	public CueTrackPositions() {
		this(new EbmlValue());
	}
}
