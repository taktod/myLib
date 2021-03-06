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
 * Cues
 * @author taktod
 */
public class Cues extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public Cues(EbmlValue size) {
		super(Type.Cues, size);
	}
	/**
	 * constructor
	 */
	public Cues() {
		this(new EbmlValue());
	}
}
