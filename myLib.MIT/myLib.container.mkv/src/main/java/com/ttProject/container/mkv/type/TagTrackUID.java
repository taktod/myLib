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
 * TagTrackUID
 * @author taktod
 */
public class TagTrackUID extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public TagTrackUID(EbmlValue size) {
		super(Type.TagTrackUID, size);
	}
	/**
	 * constructor
	 */
	public TagTrackUID() {
		this(new EbmlValue());
	}
}
