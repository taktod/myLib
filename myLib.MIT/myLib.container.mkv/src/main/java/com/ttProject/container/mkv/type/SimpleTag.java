/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SimpleTagタグ
 * @author taktod
 */
public class SimpleTag extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SimpleTag(EbmlValue size) {
		super(Type.SimpleTag, size);
	}
	/**
	 * コンストラクタ
	 */
	public SimpleTag() {
		this(new EbmlValue());
	}
	/**
	 * 構築する
	 * @param name
	 * @param value
	 * @throws Exception
	 */
	public void setup(String name, String value) throws Exception {
		TagName tagName = new TagName();
		tagName.setValue(name);
		addChild(tagName);
		
		TagString tagString = new TagString();
		tagString.setValue(value);
		addChild(tagString);
	}
}
