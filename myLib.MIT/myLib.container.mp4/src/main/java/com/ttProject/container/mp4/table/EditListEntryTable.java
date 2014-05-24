/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.table;

import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;

/**
 * elstに保持している、editListEntryTable
 * @author taktod
 */
@SuppressWarnings("unused")
public class EditListEntryTable {
	private Bit segmentDuration;
	private Bit mediaTime;
	private Bit16 mediaRateInteger;
	private Bit16 mediaRateFraction;
}
