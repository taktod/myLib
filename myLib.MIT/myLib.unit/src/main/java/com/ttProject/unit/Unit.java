package com.ttProject.unit;

import java.nio.ByteBuffer;

/**
 * すべてのメディアunitの基本となるクラス
 * こういうクラスの参照データは、abstractクラスに持たせておいたほうが見通しがよくなりそう。
 * @author taktod
 */
public abstract class Unit implements IUnit {
	/** データサイズ */
	private int size;
	/** 保持データ */
	private ByteBuffer data;
	/** pts値 */
	private long pts;
	/** timebase値*/
	private long timebase;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return size;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		return data;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getPts() {
		return pts;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimebase() {
		return timebase;
	}
	protected void setSize(int size) {
		this.size = size;
	}
	protected void setData(ByteBuffer data) {
		this.data = data;
	}
	protected void setPts(long pts) {
		this.pts = pts;
	}
	protected void setTimebase(long timebase) {
		this.timebase = timebase;
	}
}
