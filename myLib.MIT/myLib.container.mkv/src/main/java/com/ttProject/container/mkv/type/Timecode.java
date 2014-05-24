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
 * Timecodeタグ
 * @author taktod
 */
public class Timecode extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Timecode(EbmlValue size) {
		super(Type.Timecode, size);
	}
	/**
	 * コンストラクタ
	 */
	public Timecode() {
		this(new EbmlValue());
	}
}
