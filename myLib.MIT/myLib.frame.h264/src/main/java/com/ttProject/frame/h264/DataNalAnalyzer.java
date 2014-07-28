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
 * flvやh264の様なsize + dataのnalを解析する動作
 * 実体の読み込みまで実施します。
 * @author taktod
 */
public class DataNalAnalyzer extends H264FrameAnalyzer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(DataNalAnalyzer.class);
	/** configDataが決定しないと、nalSizeの取得方法が決定しないみたいです。 */
	private ConfigData configData = null;
	/**
	 * configDataをセットしておきます
	 * @param configData
	 */
	public void setConfigData(ConfigData configData) {
		this.configData = configData;
	}
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
