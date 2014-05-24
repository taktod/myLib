/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.ExpGolomb;

/**
 * signed exp-golomb
 * @author taktod
 */
public class Seg extends ExpGolomb {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get() {
		int value = super.getData();
		if((value & 0x01) == 1) {
			return -1 * (value >>> 1);
		}
		else {
			return (value >>> 1);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		if(value > 0) {
			super.setData(value << 1);
		}
		else {
			super.setData((-1 * value) << 1 | 1);
		}
	}
}
