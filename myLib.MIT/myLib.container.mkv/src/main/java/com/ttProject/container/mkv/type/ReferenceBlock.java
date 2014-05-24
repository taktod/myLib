/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;


import com.ttProject.container.mkv.MkvSignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * ReferenceBlockタグ
 * @author taktod
 */
public class ReferenceBlock extends MkvSignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ReferenceBlock(EbmlValue size) {
		super(Type.ReferenceBlock, size);
	}
	/**
	 * コンストラクタ
	 */
	public ReferenceBlock() {
		this(new EbmlValue());
	}
}
