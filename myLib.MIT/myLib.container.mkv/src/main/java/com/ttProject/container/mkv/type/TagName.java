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
 * TagName
 * @author taktod
 */
public class TagName extends MkvUtf8Tag {
	/**
	 * constructor
	 * @param size
	 */
	public TagName(EbmlValue size) {
		super(Type.TagName, size);
	}
	/**
	 * constructor
	 */
	public TagName() {
		this(new EbmlValue());
	}
}
