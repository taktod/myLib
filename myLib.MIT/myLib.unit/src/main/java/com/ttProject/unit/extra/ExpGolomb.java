/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit7;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * to handle expGolomb
 * @see http://en.wikipedia.org/wiki/Exponential-Golomb_coding
 * @author taktod
 */
public abstract class ExpGolomb extends Bit {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(ExpGolomb.class);
	/** value */
	private int value = 0;
	/** zeroCount */
	private int zeroCount = 0;
	/** flag for found on bit1 */
	private boolean find1Flg = false;
	/** holding bit list */
	protected final List<Bit> bits = new ArrayList<Bit>();
	/**
	 * constructor
	 */
	public ExpGolomb() {
		super(0);
		bitCount = 1;
		bits.add(new Bit1(1));
	}
	/**
	 * ref the value
	 * @return
	 */
	protected int getData() {
		return value;
	}
	/**
	 * set the value
	 * @param value
	 */
	protected void setData(int value) {
		this.value = value;
		bits.clear();
		int data = value;
		bitCount = 0;
		int i;
		for(i = 0;data != 0; data >>= 1, i ++) {
			bits.add(0, new Bit1(data & 0x01));
			bitCount ++;
		}
		int zeroCount = i - 1;
		for(;zeroCount >= 8;zeroCount -= 8) {
			bits.add(0, new Bit8());
			bitCount += 8;
		}
		bitCount += zeroCount;
		switch(zeroCount) {
		case 1:
			bits.add(0, new Bit1());
			break;
		case 2:
			bits.add(0, new Bit2());
			break;
		case 3:
			bits.add(0, new Bit3());
			break;
		case 4:
			bits.add(0, new Bit4());
			break;
		case 5:
			bits.add(0, new Bit5());
			break;
		case 6:
			bits.add(0, new Bit6());
			break;
		case 7:
			bits.add(0, new Bit7());
			break;
		default:
			break;
		}
	}
	/**
	 * add bit1
	 * @param bit append bit1
	 * @return false:no more true:need more
	 */
	public boolean addBit1(Bit1 bit) {
		if(!find1Flg) {
			// first zero part/
			if(bit.get() == 0) {
				zeroCount ++;
			}
			else {
				// found
				find1Flg = true;
//				bitCount = zeroCount * 2 + 1;
				value = 1;
				// after this data body.
			}
		}
		else {
			value = (value << 1) | bit.get();
			zeroCount --;
		}
		boolean end = zeroCount == 0;
		if(end) {
			setData(value);
		}
		return !end;
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
