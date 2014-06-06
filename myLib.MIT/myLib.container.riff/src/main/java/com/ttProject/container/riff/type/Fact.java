/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.RiffUnit;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * fact情報
 * @author taktod
 */
public class Fact extends RiffUnit {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Fact.class);
	private Bit32 totalSampleNum = new Bit32();
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(totalSampleNum);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
