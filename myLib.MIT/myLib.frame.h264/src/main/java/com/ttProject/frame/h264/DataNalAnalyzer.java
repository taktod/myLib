/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264;

import org.apache.log4j.Logger;

import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * analyzer for dataNal
 * ex:flv mp4
 * @author taktod
 */
public class DataNalAnalyzer extends H264FrameAnalyzer {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(DataNalAnalyzer.class);
	/** configData is required to get nalSizeBytes */
	private ConfigData configData = null;
	/**
	 * set the configData
	 * @param configData
	 */
	public void setConfigData(ConfigData configData) {
		this.configData = configData;
	}
	@Override
	public void setPrivateData(IReadChannel channel) throws Exception {
		ConfigData configData = new ConfigData();
		configData.analyzeData(channel);
		this.configData = configData;
	}
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
			throw new Exception("channel data is too short for size read.");
		}
		int size = BufferUtil.safeRead(channel, configData.getNalSizeBytes()).getInt();
		if(size <= 0) {
			throw new Exception("load size is negative.");
		}
		if(channel.size() - channel.position() < size) {
			throw new Exception("channel data is too short.");
		}
		return setupFrame(BufferUtil.safeRead(channel, size));
	}
}
