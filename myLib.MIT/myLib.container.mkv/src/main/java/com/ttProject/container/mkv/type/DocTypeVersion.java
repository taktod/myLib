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
 * DocTypeVersionタグ
 * @author taktod
 */
public class DocTypeVersion extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DocTypeVersion(EbmlValue size) {
		super(Type.DocTypeVersion, size);
	}
	/**
	 * コンストラクタ
	 */
	public DocTypeVersion() {
		this(new EbmlValue());
	}
}
