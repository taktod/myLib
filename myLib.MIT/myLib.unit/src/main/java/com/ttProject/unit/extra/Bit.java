/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra;

import com.ttProject.util.BitUtil;

/**
 * base for bit.
 * @author taktod
 */
public abstract class Bit {
	/** data */
	private byte value;
	/** num of bits */
	protected int bitCount;
	/**
	 * constructor
	 * @param count
	 */
	public Bit(int count) {
		bitCount = count;
	}
	/**
	 * set the value
	 * @param value
	 */
	public void set(int value) {
		this.value = (byte)value;
	}
	/**
	 * ref the value
	 * @return
	 */
	public int get() {
		return value & 0xFF;
	}
	/**
	 * ref the bit count。
	 * @return
	 */
	public int getBitCount() {
		return bitCount;
	}
	/**
	 * dump the data.
	 * @return 
	 */
	@Override
	public String toString() {
		return BitUtil.toBit(value, bitCount);
	}
}
