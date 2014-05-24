/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvFloatTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Durationタグ
 * @author taktod
 */
public class Duration extends MkvFloatTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Duration(EbmlValue size) {
		super(Type.Duration, size);
	}
	/**
	 * コンストラクタ
	 */
	public Duration() {
		this(new EbmlValue());
	}
}
