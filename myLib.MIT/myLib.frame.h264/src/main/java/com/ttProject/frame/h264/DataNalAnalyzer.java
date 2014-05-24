/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264;

import org.apache.log4j.Logger;

import com.ttProject.frame.IFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvやh264の様なsize + dataのnalを解析する動作
 * 実体の読み込みまで実施します。
 * @author taktod
 */
public class DataNalAnalyzer extends VideoAnalyzer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(DataNalAnalyzer.class);
	/** 現在処理フレーム */
	private H264Frame h264Frame = null;
	/**
	 * コンストラクタ
	 */
	public DataNalAnalyzer() {
		super(new H264FrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		if(channel.size() < 4) {
			throw new Exception("読み込みバッファ量がおかしいです。");
		}
		int size = BufferUtil.safeRead(channel, 4).getInt();
		if(size <= 0) {
			throw new Exception("データ指定がおかしいです。");
		}
		if(channel.size() - channel.position() < size) {
			throw new Exception("データが足りません");
		}
		IReadChannel byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, size));
		H264Frame frame = (H264Frame)getSelector().select(byteChannel);
		frame.load(byteChannel);
		if(h264Frame == null || h264Frame.getClass() != frame.getClass() || (frame instanceof SliceFrame && ((SliceFrame)frame).getFirstMbInSlice() == 0)) {
			// 1つ前のデータを応答しますので、保持しておく。
			IFrame oldFrame = h264Frame;
			if(oldFrame == null) { // 初データの場合はNullFrameを応答する
				oldFrame = NullFrame.getInstance();
			}
			h264Frame = frame;
			h264Frame.addFrame(frame);
			return oldFrame;
		}
		else {
			// 中途データの場合は強制的にNullFrame応答でOK
			h264Frame.addFrame(frame);
			return NullFrame.getInstance();
		}
	}
	@Override
	public IFrame getRemainFrame() throws Exception {
		H264Frame frame = h264Frame;
		h264Frame = null;
		return frame;
	}
}
