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
 * FlagDefault
 * @author taktod
 */
public class FlagDefault extends MkvUtf8Tag {
	/**
	 * constructor
	 * @param size
	 */
	public FlagDefault(EbmlValue size) {
		super(Type.FlagDefault, size);
	}
	/**
	 * constructor
	 */
	public FlagDefault() {
		this(new EbmlValue());
	}
}
