package com.ttProject.unit.extra;

/**
 * unsigned exp-golomb
 * @author taktod
 */
public class Ueg extends ExpGolomb {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get() {
		return super.get() - 1;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int val) {
		super.set(val + 1);
	}
}
