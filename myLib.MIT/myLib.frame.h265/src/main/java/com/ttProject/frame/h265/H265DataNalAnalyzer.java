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
 * mkvやmp4等のh265のsize + dataのnalを解析する動作
 * @author taktod
 */
public class H265DataNalAnalyzer extends H265FrameAnalyzer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(H265DataNalAnalyzer.class);
	/** 共通で利用するconfigData値 */
	private ConfigData configData = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		if(channel.size() < configData.getNalSizeBytes()) {
			throw new Exception("読み込みバッファ量がおかしいです。");
		}
		int size = BufferUtil.safeRead(channel, configData.getNalSizeBytes()).getInt();
		if(size <= 0) {
			throw new Exception("データ指定がおかしいです。");
		}
		if(channel.size() - channel.position() < size) {
			throw new Exception("データが足りません");
		}
		return setupFrame(BufferUtil.safeRead(channel, size));
	}
}
