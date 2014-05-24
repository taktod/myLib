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
 * EBMLVersionタグ
 * @author taktod
 */
public class EBMLVersion extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public EBMLVersion(EbmlValue size) {
		super(Type.EBMLVersion, size);
	}
	/**
	 * コンストラクタ
	 */
	public EBMLVersion() {
		this(new EbmlValue());
	}
}
