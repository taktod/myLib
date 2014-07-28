/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264;

import java.nio.ByteBuffer;

import com.ttProject.frame.IFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.VideoSelector;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * h264のデフォルトのAnalyzer
 * Nal解析と同じにしておきます
 * @author taktod
 */
public abstract class H264FrameAnalyzer extends VideoAnalyzer {
	/** 現在処理中のフレーム */
	private H264Frame h264Frame = null;
	/**
	 * コンストラクタ
	 * @param selector
	 */
	public H264FrameAnalyzer(VideoSelector selector) {
		super(selector);
	}
	/**
	 * frameの内容をセットアップする
	 * @param buffer
	 * @return
	 * @throws Exceptions
	 */
	protected IFrame setupFrame(ByteBuffer buffer) throws Exception {
		IReadChannel channel = new ByteReadChannel(buffer);
		H264Frame frame = (H264Frame) getSelector().select(channel);
		// フレームを読み込んだらとりあえず、loadしておこうか
		if(frame != null) {
			frame.load(channel);
		}
		if(h264Frame == null || h264Frame.getClass() != frame.getClass() || (frame instanceof SliceFrame && ((SliceFrame)frame).getFirstMbInSlice() == 0)) {
			IFrame oldFrame = h264Frame;
			if(oldFrame == null) {
				oldFrame = NullFrame.getInstance();
			}
			h264Frame = frame;
			h264Frame.addFrame(frame);
			return oldFrame;
		}
		else {
			h264Frame.addFrame(frame);
			return NullFrame.getInstance();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame getRemainFrame() throws Exception {
		H264Frame frame = h264Frame;
		h264Frame = null;
		return frame;
	}
}
