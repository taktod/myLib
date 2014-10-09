package com.ttProject.container.riff;

import java.util.List;

/**
 * RiffMasterUnit
 * base for riff unit with child units.
 * @author taktod
 */
public abstract class RiffMasterUnit extends RiffSizeUnit {
	private List<RiffUnit> unitsList; // childUnitList.
	/**
	 * constructor
	 * @param type
	 */
	public RiffMasterUnit(Type type) {
		super(type);
	}
}
