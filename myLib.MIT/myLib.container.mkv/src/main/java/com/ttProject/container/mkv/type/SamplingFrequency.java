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
 * SamplingFrequencyタグ
 * @author taktod
 */
public class SamplingFrequency extends MkvFloatTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SamplingFrequency(EbmlValue size) {
		super(Type.SamplingFrequency, size);
	}
	/**
	 * コンストラクタ
	 */
	public SamplingFrequency() {
		this(new EbmlValue());
	}
}
