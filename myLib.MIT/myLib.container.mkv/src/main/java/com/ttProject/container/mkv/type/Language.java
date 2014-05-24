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
 * Languageタグ
 * @author taktod
 */
public class Language extends MkvStringTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Language(EbmlValue size) {
		super(Type.Language, size);
	}
	/**
	 * コンストラクタ
	 */
	public Language() {
		this(new EbmlValue());
	}
}
