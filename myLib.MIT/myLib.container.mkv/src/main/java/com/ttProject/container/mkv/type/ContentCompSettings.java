/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * ContentCompSettings
 * @author taktod
 */
public class ContentCompSettings extends MkvBinaryTag {
	/**
	 * constructor
	 * @param size
	 */
	public ContentCompSettings(EbmlValue size) {
		super(Type.ContentCompSettings, size);
	}
	/**
	 * constructor
	 */
	public ContentCompSettings() {
		this(new EbmlValue());
	}
}
