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
 * BlockGroupタグ
 * @author taktod
 */
public class BlockGroup extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public BlockGroup(EbmlValue size) {
		super(Type.BlockGroup, size);
	}
	/**
	 * コンストラクタ
	 */
	public BlockGroup() {
		this(new EbmlValue());
	}
}
