/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h265;

import java.nio.ByteBuffer;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * h265のデフォルトのAnalyzer
 * Nal解析と同じにしておきます。
 * @author taktod
 */
public abstract class H265FrameAnalyzer extends VideoAnalyzer {
	/** 現在処理中のフレーム */
	private H265Frame h265Frame = null;
	/**
	 * コンストラクタ
	 */
	public H265FrameAnalyzer() {
		super(new H265FrameSelector());
	}
	/**
	 * frameの内容をセットアップする
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	protected IFrame setupFrame(ByteBuffer buffer) throws Exception {
		IReadChannel channel = new ByteReadChannel(buffer);
		H265Frame frame = (H265Frame) getSelector().select(channel);
		frame.load(channel);
		if(h265Frame == null || h265Frame.getClass() != frame.getClass()) { // firstMbInSliceみて、連続するフレームであるか確認した方がいいかもしれない。
			IFrame oldFrame = h265Frame;
			if(oldFrame == null) {
				oldFrame = NullFrame.getInstance();
			}
			h265Frame = frame;
			h265Frame.addFrame(frame);
			return oldFrame;
		}
		else {
			h265Frame.addFrame(frame);
			return NullFrame.getInstance();
		}
	}
	@Override
	public IFrame getRemainFrame() throws Exception {
		IFrame frame = h265Frame;
		h265Frame = null;
		return frame;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.H265;
	}
}
