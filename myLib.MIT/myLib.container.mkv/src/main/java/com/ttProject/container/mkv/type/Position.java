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
 * Position
 * @author taktod
 */
public class Position extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public Position(EbmlValue size) {
		super(Type.Position, size);
	}
	/**
	 * constructor
	 */
	public Position() {
		this(new EbmlValue());
	}
}
