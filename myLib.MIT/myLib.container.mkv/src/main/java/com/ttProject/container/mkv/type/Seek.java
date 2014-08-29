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
 * Seekタグ
 * @author taktod
 */
public class Seek extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Seek(EbmlValue size) {
		super(Type.Seek, size);
	}
	/**
	 * コンストラクタ
	 */
	public Seek() {
		this(new EbmlValue());
	}
}
