/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.ogg;

import com.ttProject.container.Reader;

/**
 * reader for ogg container.
 * @author taktod
 */
public class OggPageReader extends Reader {
	/**
	 * constructor
	 */
	public OggPageReader() {
		super(new OggPageSelector());
	}
}
