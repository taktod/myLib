/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.flv1;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.Manager;
import com.ttProject.nio.channels.IReadChannel;

public class Flv1Manager extends Manager<Frame> {
	@Override
	public Frame getUnit(IReadChannel source) throws Exception {
		return null;
	}
	@Override
	public List<Frame> getUnits(ByteBuffer data) throws Exception {
		return null;
	}
}
