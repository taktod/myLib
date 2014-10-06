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
 * CueTrack
 * @author taktod
 */
public class CueTrack extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public CueTrack(EbmlValue size) {
		super(Type.CueTrack, size);
	}
	/**
	 * constructor
	 */
	public CueTrack() {
		this(new EbmlValue());
	}
}
