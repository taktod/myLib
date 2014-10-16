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
 * **wb
 * audio data
 * @author taktod
 */
public class Wb extends RiffFrameUnit {
	/**
	 * constructor
	 */
	public Wb(int dataValue) {
		super(dataValue, Type.wb);
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
