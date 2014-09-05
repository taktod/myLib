package com.ttProject.unit;

import java.util.Comparator;

/**
 * containerやframeのソート用クラス
 * @author taktod
 */
public class UnitComparator implements Comparator<Unit> {
	@Override
	public int compare(Unit unit1, Unit unit2) {
		return (int)(unit1.getPts() - unit2.getPts());
	}
}
