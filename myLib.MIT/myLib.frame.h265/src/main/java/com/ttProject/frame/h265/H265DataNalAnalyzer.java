/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h265;

import org.apache.log4j.Logger;

import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * analyzer for h265 dataNal.
 * @author taktod
 */
public class H265DataNalAnalyzer extends H265FrameAnalyzer {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(H265DataNalAnalyzer.class);
	/** configData for share */
	private ConfigData configData = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		if(configData == null) {
			throw new Exception("configData is undefined.");
		}
		if(channel.size() < configData.getNalSizeBytes()) {
			throw new Exception("reading buffer size is too short to get size.");
		}
		int size = BufferUtil.safeRead(channel, configData.getNalSizeBytes()).getInt();
		if(size <= 0) {
			throw new Exception("size information is negative.");
		}
		if(channel.size() - channel.position() < size) {
			throw new Exception("data size is too short.");
		}
		return setupFrame(BufferUtil.safeRead(channel, size));
	}
}
