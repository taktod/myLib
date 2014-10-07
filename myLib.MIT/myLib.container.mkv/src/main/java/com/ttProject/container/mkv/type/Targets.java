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
 * Targets
 * @author taktod
 */
public class Targets extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public Targets(EbmlValue size) {
		super(Type.Targets, size);
	}
	/**
	 * constructor
	 */
	public Targets() {
		this(new EbmlValue());
	}
}
