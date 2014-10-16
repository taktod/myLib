/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffSizeUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * idx1
 * data for seeking.
 * @author taktod
 */
public class Idx1 extends RiffSizeUnit {
	/**
	 * constructor
	 */
	public Idx1() {
		super(Type.idx1);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		BufferUtil.quickDispose(channel, getSize() - 8);
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
