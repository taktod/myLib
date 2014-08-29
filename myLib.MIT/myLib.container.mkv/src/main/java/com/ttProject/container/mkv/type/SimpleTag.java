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
}
