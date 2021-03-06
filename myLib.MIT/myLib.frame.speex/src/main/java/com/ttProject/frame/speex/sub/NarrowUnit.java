/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex.sub;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit7;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * NarrowUnit
 * @author taktod
 */
public class NarrowUnit extends SubUnit {
	private static int[] bitSizeList = {5, 43, 119, 160, 220, 300, 364, 492, 79};
	private Bit4 modeId = new Bit4();
	private List<Bit> bitList = new ArrayList<Bit>();
	private int size = 5;
	/**
	 * constructor
	 */
	public NarrowUnit() {
		bitList.add(new Bit1(0));
	}
	@Override
	public void load(BitLoader loader) throws Exception {
		// first, load modeId
		loader.load(modeId);
		bitList.add(modeId);
		size = bitSizeList[modeId.get()];
		int left = size - 5;
		while(left > 0) {
			Bit bit = null;
			switch(left) {
			case 1:bit = new Bit1();left -= 1;break;
			case 2:bit = new Bit2();left -= 2;break;
			case 3:bit = new Bit3();left -= 3;break;
			case 4:bit = new Bit4();left -= 4;break;
			case 5:bit = new Bit5();left -= 5;break;
			case 6:bit = new Bit6();left -= 6;break;
			case 7:bit = new Bit7();left -= 7;break;
			default:
				bit = new Bit8();
				left -= 8;
				break;
			}
			loader.load(bit);
			bitList.add(bit);
		}
	}
	@Override
	public int getBitCount() {
		return size;
	}
	@Override
	public List<Bit> getBitList() {
		return bitList;
	}
}
