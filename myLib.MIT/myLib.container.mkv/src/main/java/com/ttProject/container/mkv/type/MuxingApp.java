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
 * MuxingApp
 * @author taktod
 */
public class MuxingApp extends MkvUtf8Tag {
	/**
	 * constructor
	 * @param size
	 */
	public MuxingApp(EbmlValue size) {
		super(Type.MuxingApp, size);
	}
	/**
	 * constructor
	 */
	public MuxingApp() {
		this(new EbmlValue());
	}
}
