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
 * MaxBlockAdditionIDタグ
 * @author taktod
 */
public class MaxBlockAdditionID extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public MaxBlockAdditionID(EbmlValue size) {
		super(Type.MaxBlockAdditionID, size);
	}
	/**
	 * コンストラクタ
	 */
	public MaxBlockAdditionID() {
		this(new EbmlValue());
	}
}
