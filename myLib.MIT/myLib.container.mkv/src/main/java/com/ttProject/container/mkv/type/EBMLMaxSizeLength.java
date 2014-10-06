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
 * EBMLMaxSizeLength
 * @author taktod
 */
public class EBMLMaxSizeLength extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public EBMLMaxSizeLength(EbmlValue size) {
		super(Type.EBMLMaxSizeLength, size);
	}
	/**
	 * constructor
	 */
	public EBMLMaxSizeLength() {
		this(new EbmlValue());
	}
}
