/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra;

/**
 * base of crc32,(for mpegts, ogg,...)
 * @author taktod
 */
public class Crc32 {
	private long POLYNOMINAL = 0x04C11DB7L;
	private long[] crcTable = new long[256];
	protected long crc;
	/**
	 * constructor
	 */
	public Crc32() {
		// make table
		long crc = 0;
		for(int i = 0;i < 256;i ++) {
			crc = i << 24;
			for(int j = 0;j < 8;j ++) {
				crc = (crc << 1) ^ ((crc & 0x80000000L) != 0 ? POLYNOMINAL : 0);
			}
			crcTable[i] = crc & 0xFFFFFFFFL;
		}
		// put the initial data.
		reset();
	}
	/**
	 * reset
	 */
	public void reset() {
		crc = 0;
	}
	/**
	 * put the data.
	 * @param b
	 */
	public void update(byte b) {
		crc = (crc << 8) ^ crcTable[(int)(((crc >> 24) ^ b) & 0xFF)];
	}
	/**
	 * get the calcurated value.
	 * @return
	 */
	public long getValue() {
		return crc & 0xFFFFFFFFL;
	}
}
