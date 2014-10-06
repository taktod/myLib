/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv;

import com.ttProject.container.Reader;

/**
 * flvTagReader
 * @author taktod
 */
public class FlvTagReader extends Reader {
	public FlvTagReader() {
		super(new FlvTagSelector());
	}
}
