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
 * DocTypeReadVersion
 * @author taktod
 */
public class DocTypeReadVersion extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public DocTypeReadVersion(EbmlValue size) {
		super(Type.DocTypeReadVersion, size);
	}
	/**
	 * constructor
	 */
	public DocTypeReadVersion() {
		this(new EbmlValue());
	}
}
