/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.aac;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.aac.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * selector for dsi based aac.
 * @author taktod
 */
public class AacDsiFrameSelector extends AudioSelector {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AacDsiFrameSelector.class);
	/** global dsi */
	private DecoderSpecificInfo dsi = null;
	/**
	 * set decoderSpecificInfo
	 * @param dsi
	 */
	protected void setDecoderSpecificInfo(DecoderSpecificInfo dsi) {
		this.dsi = dsi;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(dsi == null) {
			throw new Exception("dsi is undefined.");
		}
		Frame frame = new Frame();
		setup(frame);
		frame.loadDecoderSpecificInfo(channel.size(), dsi, channel);
		return frame;
	}
}
