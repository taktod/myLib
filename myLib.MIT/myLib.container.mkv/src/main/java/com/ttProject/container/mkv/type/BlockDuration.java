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
 * BlockDurationタグ
 * @author taktod
 */
public class BlockDuration extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public BlockDuration(EbmlValue size) {
		super(Type.BlockDuration, size);
	}
	/**
	 * コンストラクタ
	 */
	public BlockDuration() {
		this(new EbmlValue());
	}
}
