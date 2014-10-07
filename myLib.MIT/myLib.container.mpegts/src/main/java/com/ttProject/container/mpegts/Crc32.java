/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts;

/**
 * crc32 for mpegts
 * @author taktod
 */
public class Crc32 extends com.ttProject.unit.extra.Crc32{
	/**
	 * intialize
	 */
	public void reset() {
		crc = 0xFFFFFFFFL;
	}
}
