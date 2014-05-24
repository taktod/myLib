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
 * DocTypeReadVersionタグ
 * @author taktod
 */
public class DocTypeReadVersion extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DocTypeReadVersion(EbmlValue size) {
		super(Type.DocTypeReadVersion, size);
	}
	/**
	 * コンストラクタ
	 */
	public DocTypeReadVersion() {
		this(new EbmlValue());
	}
}
