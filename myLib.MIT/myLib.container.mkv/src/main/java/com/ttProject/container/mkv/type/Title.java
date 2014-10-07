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
 * Title
 * @author taktod
 */
public class Title extends MkvUtf8Tag {
	/**
	 * constructor
	 * @param size
	 */
	public Title(EbmlValue size) {
		super(Type.Title, size);
	}
	/**
	 * constructor
	 */
	public Title() {
		this(new EbmlValue());
	}
}
