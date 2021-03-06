/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mpegts;

/**
 * mpegtsのcrc32の計算動作
 * @author taktod
 */
public class Crc32 {
	private long MPEG2TS_POLYNOMINAL = 0x04C11DB7L;
	private long[] crcTable = new long[256];
	long crc;
	/**
	 * コンストラクタ
	 */
	public Crc32() {
		// テーブルをつくっておく。
		long crc = 0;
		for(int i = 0;i < 256;i ++) {
			crc = i << 24;
			for(int j = 0;j < 8;j ++) {
				crc = (crc << 1) ^ ((crc & 0x80000000L) != 0 ? MPEG2TS_POLYNOMINAL : 0);
			}
			crcTable[i] = crc & 0xFFFFFFFFL;
		}
		reset();
	}
	/**
	 * 初期化(設定によると0xFFFFFFFFにしていないとだめっぽい)
	 */
	public void reset() {
		crc = 0xFFFFFFFFL;
	}
	/**
	 * データを追加していきます。
	 * @param b
	 */
	public void update(byte b) {
		crc = (crc << 8) ^ crcTable[(int)(((crc >> 24) ^ b) & 0xFF)];
	}
	/**
	 * 計算済みのデータを取得します。
	 * @return
	 */
	public long getValue() {
		return crc & 0xFFFFFFFFL;
	}
}
