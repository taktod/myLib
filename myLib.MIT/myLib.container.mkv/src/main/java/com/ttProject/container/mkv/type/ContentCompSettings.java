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
 * ContentCompSettingsタグ
 * @author taktod
 */
public class ContentCompSettings extends MkvBinaryTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ContentCompSettings(EbmlValue size) {
		super(Type.ContentCompSettings, size);
	}
	/**
	 * コンストラクタ
	 */
	public ContentCompSettings() {
		this(new EbmlValue());
	}
}
