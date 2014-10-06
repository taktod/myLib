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
 * EBMLReadVersion
 * @author taktod
 */
public class EBMLReadVersion extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public EBMLReadVersion(EbmlValue size) {
		super(Type.EBMLReadVersion, size);
	}
	/**
	 * constructor
	 */
	public EBMLReadVersion() {
		this(new EbmlValue());
	}
}
