/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import com.ttProject.container.Reader;

/**
 * riffのunit読み込み動作
 * @author taktod
 */
public class RiffUnitReader extends Reader {
	/**
	 * コンストラクタ
	 */
	public RiffUnitReader() {
		super(new RiffUnitSelector());
	}
}
