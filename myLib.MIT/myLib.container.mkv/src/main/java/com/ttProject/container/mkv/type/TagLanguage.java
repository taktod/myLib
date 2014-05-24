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
 * TagLanguageタグ
 * @author taktod
 */
public class TagLanguage extends MkvStringTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TagLanguage(EbmlValue size) {
		super(Type.TagLanguage, size);
	}
	/**
	 * コンストラクタ
	 */
	public TagLanguage() {
		this(new EbmlValue());
	}
}
