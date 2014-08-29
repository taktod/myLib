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
 * Targetsタグ
 * @author taktod
 */
public class Targets extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Targets(EbmlValue size) {
		super(Type.Targets, size);
	}
	/**
	 * コンストラクタ
	 */
	public Targets() {
		this(new EbmlValue());
	}
}
