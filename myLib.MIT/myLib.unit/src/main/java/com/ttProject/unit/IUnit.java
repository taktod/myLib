/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit;

/**
 * interface for units.
 * unit is part of data.
 * @author taktod
 */
public interface IUnit extends IData {
	/**
	 * pts value.(presentationTimestamp)
	 * @return
	 */
	public long getPts();
	/**
	 * timebase for units.
	 * 
	 * ex flv is 1 millisec is base, so timebase = 1000.
	 * 1sec = 1000ts.
	 * ex mpegts is 1/90000 sec is base, so timebase = 90000.
	 * @return
	 */
	public long getTimebase();
}
