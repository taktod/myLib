/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp3;

import com.ttProject.container.Reader;

/**
 * unit reader.
 * @author taktod
 */
public class Mp3UnitReader extends Reader {
	/**
	 * constructor
	 */
	public Mp3UnitReader() {
		super(new Mp3UnitSelector());
	}
}
