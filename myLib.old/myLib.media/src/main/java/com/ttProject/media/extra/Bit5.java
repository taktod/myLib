/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.extra;

/**
 * 5ビットを表現する型
 * @author taktod
 */
public class Bit5 extends Bit {
	/**
	 * コンストラクタ
	 */
	public Bit5() {
		this(0);
	}
	/**
	 * コンストラクタ
	 * @param value
	 */
	public Bit5(int value) {
		super(5);
		set(value);
	}
	/**
	 * データ設定
	 * @param value
	 */
	public void set(int value) {
		super.set(value & 0x1F);
	}
}
