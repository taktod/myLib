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
 * EBMLタグ
 * @author taktod
 */
public class EBML extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public EBML(EbmlValue size) {
		super(Type.EBML, size);
	}
	/**
	 * コンストラクタ
	 */
	public EBML() {
		super(Type.EBML, new EbmlValue());
	}
}
