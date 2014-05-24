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
 * TrackUIDタグ
 * @author taktod
 */
public class TrackUID extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TrackUID(EbmlValue size) {
		super(Type.TrackUID, size);
	}
	/**
	 * コンストラクタ
	 */
	public TrackUID() {
		this(new EbmlValue());
	}
}
