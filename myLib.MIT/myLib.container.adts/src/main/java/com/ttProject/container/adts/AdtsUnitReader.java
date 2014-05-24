/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.adts;

import com.ttProject.container.Reader;

/**
 * unitのselector
 * @author taktod
 */
public class AdtsUnitReader extends Reader {
	public AdtsUnitReader() {
		super(new AdtsUnitSelector());
	}
}
