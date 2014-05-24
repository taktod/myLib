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
 * BitDepthタグ
 * @author taktod
 */
public class BitDepth extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public BitDepth(EbmlValue size) {
		super(Type.BitDepth, size);
	}
	/**
	 * コンストラクタ
	 */
	public BitDepth() {
		this(new EbmlValue());
	}
}
