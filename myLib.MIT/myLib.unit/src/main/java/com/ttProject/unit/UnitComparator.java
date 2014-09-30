/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit;

import java.util.Comparator;

/**
 * for sort of container or frames.
 * @author taktod
 */
public class UnitComparator implements Comparator<IUnit> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(IUnit unit1, IUnit unit2) {
		return (int)(unit1.getPts() - unit2.getPts());
	}
}
