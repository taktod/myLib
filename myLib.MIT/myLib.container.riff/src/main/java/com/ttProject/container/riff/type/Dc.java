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
 * **dc
 * compressed video frame
 * the size data is frame data.
 * passed frame num shows the pts number.
 * @author taktod
 */
public class Dc extends RiffFrameUnit {
	/**
	 * constructor
	 */
	public Dc(int dataValue) {
		super(dataValue, Type.dc);
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
