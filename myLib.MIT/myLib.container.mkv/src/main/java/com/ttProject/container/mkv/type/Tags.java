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
 * Tagsタグ
 * @author taktod
 */
public class Tags extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Tags(EbmlValue size) {
		super(Type.Tags, size);
	}
	/**
	 * コンストラクタ
	 */
	public Tags() {
		this(new EbmlValue());
	}
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Tags(long position) {
		this();
		setPosition((int)position);
	}
	/**
	 * 位置を設定する
	 * @param position
	 */
	public void setPosition(long position) {
		super.setPosition((int)position);
	}
}
