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
 * slice系のframeの共通項
 * Trail
 * Tsa
 * Stsa
 * Radl
 * Rasl
 * Bla
 * Idr
 * Cra
 * あたりのnalがこれにあたるっぽい。
 * h265では、firstMbInSliceを読ませることで、複数nalにまたがるデータを処理できるようにしていた。
 * 同じようなことをやる必要あるかもしれない。
 * @author taktod
 */
public abstract class SliceFrame extends H265Frame {
	/**
	 * コンストラクタ
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
