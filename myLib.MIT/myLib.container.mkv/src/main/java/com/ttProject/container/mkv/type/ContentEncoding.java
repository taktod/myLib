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
 * ContentEncoding
 * @author taktod
 */
public class ContentEncoding extends MkvMasterTag {
	// this class seems to have only compression or encryption.
	/**
	 * constructor
	 * @param size
	 */
	public ContentEncoding(EbmlValue size) {
		super(Type.ContentEncoding, size);
	}
	/**
	 * constructor
	 */
	public ContentEncoding() {
		this(new EbmlValue());
	}
}
