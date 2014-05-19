package com.ttProject.frame.h265;

import com.ttProject.frame.VideoFrame;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * h265のframe
 * @author taktod
 */
public abstract class H265Frame extends VideoFrame {
	private final Bit1 forbiddenZeroBit;
	private final Bit6 nalUnitType;
	private final Bit6 nuhLayerId;
	private final Bit3 nuhTemporalIdPlus1;
	public H265Frame(Bit1 forbiddenZeroBit,
			Bit6 nalUnitType,
			Bit6 nuhLayerId,
			Bit3 nuhTemporalIdPlus1) {
		this.forbiddenZeroBit = forbiddenZeroBit;
		this.nalUnitType = nalUnitType;
		this.nuhLayerId = nuhLayerId;
		this.nuhTemporalIdPlus1 = nuhTemporalIdPlus1;
	}
}
