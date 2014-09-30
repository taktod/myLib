/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra;

import java.util.ArrayList;
import java.util.List;

/**
 * make bit from multiple bit.
 * over 32bit, use getLong or setLong instead of get or set.
 * @author taktod
 */
public class BitN extends Bit {
	/** bit list. */
	protected final List<Bit> bits = new ArrayList<Bit>();
	/**
	 * constructor
	 * @param bits
	 */
	public BitN(Bit ... bits) {
		super(0);
		int count = 0;
		for(Bit bit : bits) {
			if(bit == null) {
				continue;
			}
			count += bit.bitCount;
			this.bits.add(bit);
		}
		bitCount = count;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get() {
		int value = 0;
		for(Bit bit : bits) {
			value <<= bit.bitCount;
			value |= bit.get();
		}
		return value;
	}
	/**
	 * ref the data as long
	 * @return
	 */
	public long getLong() {
		long value = 0;
		for(Bit bit : bits) {
			value <<= bit.bitCount;
			if(bit instanceof BitN) {
				value |= ((BitN) bit).getLong();
			}
			else {
				value |= bit.get();
			}
		}
		return value;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		int size = bits.size();
		for(int i = size - 1;i >= 0;i --) {
			Bit bit = bits.get(i);
			bit.set(value);
			value >>>= bit.bitCount;
		}
	}
	/**
	 * set the data as long
	 * @param value
	 */
	public void setLong(long value) {
		int size = bits.size();
		for(int i = size - 1;i >= 0;i --) {
			Bit bit = bits.get(i);
			bit.set((int)(value & 0xFFFFFFFF));
			value >>>= bit.bitCount;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		for(Bit b : bits) {
			data.append(b.toString());
		}
		return data.toString();
	}
}
