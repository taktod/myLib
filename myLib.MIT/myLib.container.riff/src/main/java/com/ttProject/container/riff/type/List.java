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

/**
 * listデータ
 * とりあえず実物がないので、調査せず
 * @author taktod
 *
 */
public class List extends RiffUnit {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(List.class);
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
