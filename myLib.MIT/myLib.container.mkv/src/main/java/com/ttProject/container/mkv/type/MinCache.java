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
 * MinCache
 * @see http://lists.matroska.org/pipermail/matroska-devel/2003-March/000332.html
 * @author taktod
 */
public class MinCache extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public MinCache(EbmlValue size) {
		super(Type.MinCache, size);
	}
	/**
	 * constructor
	 */
	public MinCache() {
		this(new EbmlValue());
	}
}
