/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import com.ttProject.container.Reader;

/**
 * riff unit reader
 * @author taktod
 */
public class RiffUnitReader extends Reader {
	/**
	 * constructor
	 */
	public RiffUnitReader() {
		super(new RiffUnitSelector());
	}
}
