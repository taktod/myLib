/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.extra;

/**
 * 1ビットを表現する型
 * @author taktod
 */
public class Bit1 extends Bit {
	/**
	 * コンストラクタ
	 */
	public Bit1() {
		this(0);
	}
	/**
	 * コンストラクタ
	 * @param value
	 */
	public Bit1(int value) {
		super(1);
		set(value);
	}
	@Override
	public void set(int value) {
		super.set(value & 0x01);
	}
}
