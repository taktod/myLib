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
 * Channelsタグ
 * @author taktod
 */
public class Channels extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Channels(EbmlValue size) {
		super(Type.Channels, size);
	}
	/**
	 * コンストラクタ
	 */
	public Channels() {
		this(new EbmlValue());
	}
}
