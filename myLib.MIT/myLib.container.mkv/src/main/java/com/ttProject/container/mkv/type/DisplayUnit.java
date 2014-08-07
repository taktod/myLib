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
 * DisplayUnitタグ
 * @author taktod
 */
public class DisplayUnit extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DisplayUnit(EbmlValue size) {
		super(Type.DisplayUnit, size);
	}
	/**
	 * コンストラクタ
	 */
	public DisplayUnit() {
		this(new EbmlValue());
	}
}
