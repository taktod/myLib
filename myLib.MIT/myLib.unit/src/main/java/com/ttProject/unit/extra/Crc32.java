/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra;

/**
 * mpegtsやoggでcrc32動作があるので基本クラスをつくって、それの拡張で済ませたい
 * @author taktod
 */
public class Crc32 {
	private long POLYNOMINAL = 0x04C11DB7L;
	private long[] crcTable = new long[256];
	protected long crc;
	/**
	 * コンストラクタ
	 */
	public Crc32() {
		// テーブルをつくる。
		long crc = 0;
		for(int i = 0;i < 256;i ++) {
			crc = i << 24;
			for(int j = 0;j < 8;j ++) {
				crc = (crc << 1) ^ ((crc & 0x80000000L) != 0 ? POLYNOMINAL : 0);
			}
			crcTable[i] = crc & 0xFFFFFFFFL;
		}
		// 初期値投入
		reset();
	}
	/**
	 * 初期化
	 */
	public void reset() {
		crc = 0;
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
