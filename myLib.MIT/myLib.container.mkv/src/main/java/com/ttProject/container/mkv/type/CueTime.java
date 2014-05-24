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
 * CueTimeタグ
 * @author taktod
 */
public class CueTime extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CueTime(EbmlValue size) {
		super(Type.CueTime, size);
	}
	/**
	 * コンストラクタ
	 */
	public CueTime() {
		this(new EbmlValue());
	}
}
