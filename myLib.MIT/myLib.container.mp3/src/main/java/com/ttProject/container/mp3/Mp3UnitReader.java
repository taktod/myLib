/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp3;

import com.ttProject.container.Reader;

/**
 * unitのselector
 * @author taktod
 */
public class Mp3UnitReader extends Reader {
	public Mp3UnitReader() {
		super(new Mp3UnitSelector());
	}
}
