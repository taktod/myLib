/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.stsd;

import com.ttProject.container.Reader;

/**
 * stsdの内部atomの解析動作
 * @author taktod
 */
public class StsdAtomReader extends Reader {
	/**
	 * コンストラクタ
	 */
	public StsdAtomReader() {
		super(new StsdAtomSelector());
	}
}
