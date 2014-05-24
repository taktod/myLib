/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SegmentUIDタグ
 * @author taktod
 */
public class SegmentUID extends MkvBinaryTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SegmentUID(EbmlValue size) {
		super(Type.SegmentUID, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
