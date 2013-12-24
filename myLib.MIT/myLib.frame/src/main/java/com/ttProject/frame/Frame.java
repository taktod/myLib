package com.ttProject.frame;

import com.ttProject.unit.Unit;

/**
 * Frameの基本動作
 * @author taktod
 */
public abstract class Frame extends Unit implements IFrame {
	/** 読み込み位置 */
	private int readPosition = 0;
	/**
	 * 読み込み位置設定
	 * @param position
	 */
	protected void setReadPosition(int position) {
		this.readPosition = position;
	}
	/**
	 * 読み込み位置参照
	 * @return
	 */
	protected int getReadPosition() {
		return readPosition;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPts(long pts) {
		super.setPts(pts);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimebase(long timebase) {
		super.setTimebase(timebase);
	}
}
