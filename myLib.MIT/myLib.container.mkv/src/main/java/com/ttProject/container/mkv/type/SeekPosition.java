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
 * SeekPosition
 * @author taktod
 */
public class SeekPosition extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public SeekPosition(EbmlValue size) {
		super(Type.SeekPosition, size);
	}
	/**
	 * constructor
	 */
	public SeekPosition() {
		this(new EbmlValue());
	}
}
