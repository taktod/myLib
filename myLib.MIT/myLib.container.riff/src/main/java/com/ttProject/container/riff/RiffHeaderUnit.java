/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * Riff headerUnit
 * @author taktod
 */
public class RiffHeaderUnit extends RiffUnit {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(RiffHeaderUnit.class);
	/** format string */
	private String formatString;
	/**
	 * constructor
	 */
	public RiffHeaderUnit() {
		super(Type.RIFF);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
//		super.minimumLoad(channel);
		// load tag type.
		formatString = new String(BufferUtil.safeRead(channel, 4).array());
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {

	}
	public String getFormatString() {
		return formatString;
	}
}
