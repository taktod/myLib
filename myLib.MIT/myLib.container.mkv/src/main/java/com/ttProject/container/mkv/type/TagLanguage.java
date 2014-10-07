/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvStringTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TagLanguage
 * @author taktod
 */
public class TagLanguage extends MkvStringTag {
	/**
	 * constructor
	 * @param size
	 */
	public TagLanguage(EbmlValue size) {
		super(Type.TagLanguage, size);
	}
	/**
	 * constructor
	 */
	public TagLanguage() {
		this(new EbmlValue());
	}
}
