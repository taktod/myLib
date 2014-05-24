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
 * DisplayWidthタグ
 * @author taktod
 */
public class DisplayWidth extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DisplayWidth(EbmlValue size) {
		super(Type.DisplayWidth, size);
	}
	/**
	 * コンストラクタ
	 */
	public DisplayWidth() {
		this(new EbmlValue());
	}
}
