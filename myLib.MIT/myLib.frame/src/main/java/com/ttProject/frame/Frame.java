package com.ttProject.frame;

import com.ttProject.unit.Unit;

/**
 * Frameの基本動作
 * @author taktod
 */
public abstract class Frame extends Unit implements IFrame {
	/** 読み込み位置 */
	private int readPosition = 0;
	protected void setReadPosition(int position) {
		this.readPosition = position;
	}
	protected int getReadPosition() {
		return readPosition;
	}
	@Override
	public void setPts(long pts) {
		super.setPts(pts);
	}
	@Override
	public void setTimebase(long timebase) {
		super.setTimebase(timebase);
	}
}
