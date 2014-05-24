/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.table;

import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * trexやtraf、trunが持つ、SampleFlagsのデータ
 * @author taktod
 */
public class SampleFlags extends BitN {
	private final Bit6  reserved;
	private final Bit2  sampleDependsOn;
	private final Bit2  samplesDependedOn;
	private final Bit2  sampleHasRedundancy;
	private final Bit3  samplePaddingValue;
	private final Bit1  sampleIsDifferenceSample;
	private final Bit16 sampleDegradationPriority;
	public SampleFlags() {
		super(new Bit6(), new Bit2(), new Bit2(), new Bit2(), new Bit3(), new Bit1(), new Bit16());
		reserved                  = (Bit6)bits.get(0);
		sampleDependsOn           = (Bit2)bits.get(1);
		samplesDependedOn         = (Bit2)bits.get(2);
		sampleHasRedundancy       = (Bit2)bits.get(3);
		samplePaddingValue        = (Bit3)bits.get(4);
		sampleIsDifferenceSample  = (Bit1)bits.get(5);
		sampleDegradationPriority = (Bit16)bits.get(6);
	}
	/**
	 * 内部データdump
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("reserved:" + reserved);
		data.append(" sampleDependsOn:" + sampleDependsOn);
		data.append(" samplesDependedOn:" + samplesDependedOn);
		data.append(" sampleHasRedundancy:" + sampleHasRedundancy);
		data.append(" samplePaddingValue:" + samplePaddingValue);
		data.append(" sampleIsDifferenceSample:" + sampleIsDifferenceSample);
		data.append(" sampleDegradationPriority:" + sampleDegradationPriority);
		return super.toString();
	}
}
