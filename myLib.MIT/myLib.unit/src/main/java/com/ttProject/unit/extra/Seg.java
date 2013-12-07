package com.ttProject.unit.extra;

/**
 * signed exp-golomb
 * @author taktod
 */
public class Seg extends ExpGolomb {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get() {
		int value = super.get();
		if((value & 0x01) == 1) {
			return -1 * (value >>> 1);
		}
		else {
			return (value >>> 1);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		if(value > 0) {
			super.set(value << 1);
		}
		else {
			super.set((-1 * value) << 1 | 1);
		}
	}
}
