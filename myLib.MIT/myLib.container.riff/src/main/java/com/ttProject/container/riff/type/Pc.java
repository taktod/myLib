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
 * **pc
 * pallete change
 * @author taktod
 */
public class Pc extends RiffFrameUnit {
	public Pc(int dataValue) {
		super(dataValue, Type.pc);
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
