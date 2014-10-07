/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUtf8Tag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TagString
 * @author taktod
 */
public class TagString extends MkvUtf8Tag {
	/**
	 * constructor
	 * @param size
	 */
	public TagString(EbmlValue size) {
		super(Type.TagString, size);
	}
	/**
	 * constructor
	 */
	public TagString() {
		this(new EbmlValue());
	}
}
