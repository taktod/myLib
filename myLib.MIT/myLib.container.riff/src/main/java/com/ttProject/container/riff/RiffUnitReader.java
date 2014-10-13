/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.Reader;
import com.ttProject.nio.channels.IReadChannel;

/**
 * riff unit reader
 * @author taktod
 */
public class RiffUnitReader extends Reader {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(RiffUnitReader.class);
	/**
	 * constructor
	 */
	public RiffUnitReader() {
		super(new RiffUnitSelector());
	}
	@Override
	public IContainer read(IReadChannel channel) throws Exception {
		IContainer container = (IContainer)getSelector().select(channel);
		if(container != null) {
			if(container instanceof RiffMasterUnit) {
				((RiffMasterUnit) container).setRiffUnitReader(this);
			}
			container.load(channel);
		}
		return container;
	}
}
