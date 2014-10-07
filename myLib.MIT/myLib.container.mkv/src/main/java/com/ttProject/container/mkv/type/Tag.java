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
 * Tag
 * @author taktod
 */
public class Tag extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public Tag(EbmlValue size) {
		super(Type.Tag, size);
	}
	/**
	 * constructor
	 */
	public Tag() {
		this(new EbmlValue());
	}
}
