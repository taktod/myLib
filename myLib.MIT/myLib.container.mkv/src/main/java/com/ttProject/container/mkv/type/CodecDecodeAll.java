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
 * CodecDecodeAllタグ
 * @author taktod
 */
public class CodecDecodeAll extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CodecDecodeAll(EbmlValue size) {
		super(Type.CodecDecodeAll, size);
	}
	/**
	 * コンストラクタ
	 */
	public CodecDecodeAll() {
		this(new EbmlValue());
	}
}
