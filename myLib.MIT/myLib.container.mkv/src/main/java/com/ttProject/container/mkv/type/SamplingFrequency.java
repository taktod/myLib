/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvFloatTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SamplingFrequency
 * @author taktod
 */
public class SamplingFrequency extends MkvFloatTag {
	/**
	 * constructor
	 * @param size
	 */
	public SamplingFrequency(EbmlValue size) {
		super(Type.SamplingFrequency, size);
	}
	/**
	 * constructor
	 */
	public SamplingFrequency() {
		this(new EbmlValue());
	}
}
