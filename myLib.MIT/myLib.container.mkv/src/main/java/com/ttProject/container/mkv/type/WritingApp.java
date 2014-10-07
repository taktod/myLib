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
 * WritingApp
 * @author taktod
 */
public class WritingApp extends MkvUtf8Tag {
	/**
	 * constructor
	 * @param size
	 */
	public WritingApp(EbmlValue size) {
		super(Type.WritingApp, size);
	}
	/**
	 * constructor
	 */
	public WritingApp() {
		this(new EbmlValue());
	}
}
