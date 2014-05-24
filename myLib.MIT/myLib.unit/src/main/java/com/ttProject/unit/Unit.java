/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit;

/**
 * すべてのメディアunitの基本となるクラス
 * こういうクラスの参照データは、abstractクラスに持たせておいたほうが見通しがよくなりそう。
 * 内容構成bitデータは、絶対に入るものは、宣言時にbitを構築しておく。
 * 必要があれば入るものは、nullをいれておく。
 * コンストラクタで入るものは、finalをつけておく。
 * @author taktod
 */
public abstract class Unit extends Data implements IUnit {
	/** pts値 */
	private long pts = 0L;
	/** timebase値*/
	private long timebase = 1000L;
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
	/**
	 * pts値設定
	 * @param pts
	 */
	protected void setPts(long pts) {
		this.pts = pts;
	}
	/**
	 * timebase設定(時間はpts / timebase秒になります。)
	 * @param timebase
	 */
	protected void setTimebase(long timebase) {
		this.timebase = timebase;
	}
}
