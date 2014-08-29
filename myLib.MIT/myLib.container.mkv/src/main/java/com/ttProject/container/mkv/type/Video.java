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
 * Videoタグ
 * @author taktod
 */
public class Video extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Video(EbmlValue size) {
		super(Type.Video, size);
	}
	/**
	 * コンストラクタ
	 */
	public Video() {
		this(new EbmlValue());
	}
}
