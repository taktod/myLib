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
 * Language
 * @author taktod
 */
public class Language extends MkvStringTag {
	/**
	 * constructor
	 * @param size
	 */
	public Language(EbmlValue size) {
		super(Type.Language, size);
	}
	/**
	 * constructor
	 */
	public Language() {
		this(new EbmlValue());
	}
}
