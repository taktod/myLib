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
 * DocTypeタグ
 * @author taktod
 */
public class DocType extends MkvStringTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DocType(EbmlValue size) {
		super(Type.DocType, size);
	}
	/**
	 * コンストラクタ
	 */
	public DocType() {
		this(new EbmlValue());
	}
}
