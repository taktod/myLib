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
 * DefaultDurationタグ
 * @author taktod
 */
public class DefaultDuration extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DefaultDuration(EbmlValue size) {
		super(Type.DefaultDuration, size);
	}
	/**
	 * コンストラクタ
	 */
	public DefaultDuration() {
		this(new EbmlValue());
	}
}
