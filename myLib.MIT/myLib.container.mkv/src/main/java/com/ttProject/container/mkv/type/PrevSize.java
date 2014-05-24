/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * PrevSizeタグ
 * @author taktod
 */
public class PrevSize extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public PrevSize(EbmlValue size) {
		super(Type.PrevSize, size);
	}
	/**
	 * コンストラクタ
	 */
	public PrevSize() {
		this(new EbmlValue());
	}
}
