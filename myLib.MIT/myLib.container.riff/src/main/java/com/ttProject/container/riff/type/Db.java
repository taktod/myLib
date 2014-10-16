/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffFrameUnit;
import com.ttProject.container.riff.Type;

/**
 * **db
 * uncompressed video frame.
 * @author taktod
 */
public class Db extends RiffFrameUnit {
	/**
	 * constructor
	 */
	public Db(int dataValue) {
		super(dataValue, Type.db);
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
