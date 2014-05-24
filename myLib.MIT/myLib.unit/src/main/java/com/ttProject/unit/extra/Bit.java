/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra;

import com.ttProject.util.BitUtil;

/**
 * bit型の基本クラス
 * @author taktod
 */
public abstract class Bit {
	/** 保持データ */
	private byte value;
	/** 保持ビット数 */
	protected int bitCount;
	/**
	 * コンストラクタ
	 * @param count
	 */
	public Bit(int count) {
		bitCount = count;
	}
	/**
	 * 内部データ設定
	 * @param value
	 */
	public void set(int value) {
		this.value = (byte)value;
	}
	/**
	 * 内部データ参照
	 * @return
	 */
	public int get() {
		return value & 0xFF;
	}
	/**
	 * bit深度を参照します。
	 * @return
	 */
	public int getBitCount() {
		return bitCount;
	}
	/**
	 * データDump
	 * @return 
	 */
	@Override
	public String toString() {
		return BitUtil.toBit(value, bitCount);
	}
}
