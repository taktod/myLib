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
 * ContentEncodings
 * @author taktod
 */
public class ContentEncodings extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public ContentEncodings(EbmlValue size) {
		super(Type.ContentEncodings, size);
	}
	/**
	 * constructor
	 */
	public ContentEncodings() {
		this(new EbmlValue());
	}
}
