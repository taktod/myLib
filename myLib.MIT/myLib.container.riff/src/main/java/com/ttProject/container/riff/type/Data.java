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
import com.ttProject.frame.AudioFrame;
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
	/** 音声用の経過時刻保持 */
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
			// ここのfmt.getBlockSizeの値がpcm_alawとpcm_mulawの場合に１になって、いやな感じのデータになってしまう。
			int blockSize = 0;
			switch(fmt.getRiffCodecType()) {
			case A_LAW:
			case U_LAW:
				blockSize = 0x0100;
				if(channel.size() - channel.position() < 0x0100) {
					blockSize = channel.size() - channel.position();
				}
				break;
			default:
				blockSize = fmt.getBlockSize();
				break;
			}
			ByteReadChannel frameChannel = new ByteReadChannel(BufferUtil.safeRead(channel, blockSize));
			IAnalyzer analyzer = getFmt().getFrameAnalyzer();
			IFrame frame = analyzer.analyze(frameChannel);
			if(frame instanceof AudioFrame) {
				AudioFrame aFrame = (AudioFrame) frame;
				passedTic += aFrame.getSampleNum();
				aFrame.setPts(passedTic);
			}
			if(listener != null) {
				listener.onNewFrame(frame);
			}
		}
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
