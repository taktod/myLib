/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts;

/**
 * mpegtsのcrc32の計算動作
 * @author taktod
 */
public class Crc32 extends com.ttProject.unit.extra.Crc32{
	/**
	 * 初期化(設定によると0xFFFFFFFFにしていないとだめっぽい)
	 */
	public void reset() {
		crc = 0xFFFFFFFFL;
	}
}
