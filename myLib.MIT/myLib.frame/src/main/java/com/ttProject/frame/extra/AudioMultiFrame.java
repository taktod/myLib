/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.extra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * audioFrameを複数同時に持つ場合のframe
 * flvのaudioTagのnellymoserとかで利用します。(nellymoserでは、1,2,4ユニットが混じった動作とかあるので)
 * @author taktod
 */
public class AudioMultiFrame extends AudioFrame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AudioMultiFrame.class);
	/** 保持フレームリスト */
	private List<IAudioFrame> frameList = new ArrayList<IAudioFrame>();
	/**
	 * フレームを追加します
	 * @param frame
	 * @throws Exception
	 */
	public void addFrame(IAudioFrame frame) throws Exception {
		if(frameList.size() == 0) {
			setBit(frame.getBit());
			setChannel(frame.getChannel());
			setPts(frame.getPts());
			setTimebase(frame.getTimebase());
			setSampleRate(frame.getSampleRate());
			setSampleNum(frame.getSampleNum());
			setSize(frame.getSize());
		}
		else {
			// データの不一致はいまのところほっとく。
			setSampleNum(frame.getSampleNum() + getSampleNum()); // サンプル数は足していく。
			setSize(frame.getSize() + getSize());
		}
		frameList.add(frame);
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	protected void requestUpdate() throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public ByteBuffer getPackBuffer() {
		throw new RuntimeException("マルチフレームはpackBuffer未対応");
	}
	/**
	 * frameリスト参照
	 * @return
	 */
	public List<IAudioFrame> getFrameList() {
		return new ArrayList<IAudioFrame>(frameList);
	}
}
