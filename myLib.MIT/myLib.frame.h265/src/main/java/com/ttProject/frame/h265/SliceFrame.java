/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h265;

import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * base of slice frame
 *  Trail
 *  Tsa
 *  Stsa
 *  Radl
 *  Rasl
 *  Bla
 *  Idr
 *  Cra
 * 
 * for h264 firstMbInSlice indicate the one frame with multi nals.
 * TODO check firstMbInSlice.
 * @author taktod
 */
public abstract class SliceFrame extends H265Frame {
	/**
	 * constructor
	 * @param forbiddenZeroBit
	 * @param nalUnitType
	 * @param nuhLayerId
	 * @param nuhTemporalIdPlus1
	 */
	public SliceFrame(Bit1 forbiddenZeroBit,
			Bit6 nalUnitType,
			Bit6 nuhLayerId,
			Bit3 nuhTemporalIdPlus1) {
		super(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
	}
}
