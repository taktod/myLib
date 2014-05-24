/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUtf8Tag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * FlagDefaultタグ
 * @author taktod
 */
public class FlagDefault extends MkvUtf8Tag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public FlagDefault(EbmlValue size) {
		super(Type.FlagDefault, size);
	}
	/**
	 * コンストラクタ
	 */
	public FlagDefault() {
		this(new EbmlValue());
	}
}
