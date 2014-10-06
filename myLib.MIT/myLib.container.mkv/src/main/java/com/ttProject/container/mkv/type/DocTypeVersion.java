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
 * DocTypeVersion
 * @author taktod
 */
public class DocTypeVersion extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public DocTypeVersion(EbmlValue size) {
		super(Type.DocTypeVersion, size);
	}
	/**
	 * constructor
	 */
	public DocTypeVersion() {
		this(new EbmlValue());
	}
}
