/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.IFrameEventListener;
import com.ttProject.container.riff.RiffUnit;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * ステレオの場合はLRLRの順番らしい。
 * モノラルなら----そのまま並んでる
 * @author taktod
 */
public class Data extends RiffUnit {
	/** ロガー */
	private Logger logger = Logger.getLogger(Data.class);
	// 経過時間はこっちで調整する必要あり。
	private long passedTic = 0;
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	public void analyzeFrame(IReadChannel channel, IFrameEventListener listener) throws Exception {
		logger.info("frameを解析しようと思います。");
		Fmt fmt = getFmt();
		while(channel.position() < channel.size()) {
			ByteReadChannel frameChannel = new ByteReadChannel(BufferUtil.safeRead(channel, fmt.getBlockSize()));
			IAnalyzer analyzer = getFmt().getFrameAnalyzer();
			IFrame frame = analyzer.analyze(frameChannel);
			if(listener != null) {
				listener.onNewFrame(frame);
			}
		}
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
