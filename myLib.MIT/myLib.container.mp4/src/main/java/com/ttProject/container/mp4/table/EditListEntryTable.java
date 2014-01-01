package com.ttProject.container.mp4.table;

import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;

/**
 * elstに保持している、editListEntryTable
 * @author taktod
 */
public class EditListEntryTable {
	private Bit32 segmentDuration;
	private Bit64 segmentDuration1;
	private Bit32 mediaTime;
	private Bit64 mediaTime1;
	private Bit32 mediaRate;
}
