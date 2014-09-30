/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit;

/**
 * base for all media unit.
 * @author taktod
 */
public abstract class Unit extends Data implements IUnit {
	/** pts */
	private long pts = 0L;
	/** timebase */
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
	 * setter for pts
	 * @param pts
	 */
	protected void setPts(long pts) {
		this.pts = pts;
	}
	/**
	 * setter for timebase
	 * @param timebase
	 */
	protected void setTimebase(long timebase) {
		this.timebase = timebase;
	}
}
