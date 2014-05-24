/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.ogg;

import com.ttProject.container.Reader;

/**
 * oggのデータを解析する動作
 * @author taktod
 */
public class OggPageReader extends Reader {
	public OggPageReader() {
		super(new OggPageSelector());
	}
}
