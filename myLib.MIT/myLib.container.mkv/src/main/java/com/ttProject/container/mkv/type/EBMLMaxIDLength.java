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
 * EBMLMaxIDLength
 * @author taktod
 */
public class EBMLMaxIDLength extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public EBMLMaxIDLength(EbmlValue size) {
		super(Type.EBMLMaxIDLength, size);
	}
	/**
	 * constructor
	 */
	public EBMLMaxIDLength() {
		this(new EbmlValue());
	}
}
