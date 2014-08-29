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
 * ContentEncodingタグ
 * @author taktod
 */
public class ContentEncoding extends MkvMasterTag {
	// compressionかencryptionのどちらかを１つだけもってるっぽい。
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ContentEncoding(EbmlValue size) {
		super(Type.ContentEncoding, size);
	}
	/**
	 * コンストラクタ
	 */
	public ContentEncoding() {
		this(new EbmlValue());
	}
}
