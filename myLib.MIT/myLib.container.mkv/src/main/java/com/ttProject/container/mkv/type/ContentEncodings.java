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
 * ContentEncodingsタグ
 * @author taktod
 */
public class ContentEncodings extends MkvMasterTag {
	// contentEncodingを複数もっているらしい。
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ContentEncodings(EbmlValue size) {
		super(Type.ContentEncodings, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
