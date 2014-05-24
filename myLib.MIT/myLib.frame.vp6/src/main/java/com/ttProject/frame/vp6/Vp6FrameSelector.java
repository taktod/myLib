/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp6;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.vp6.type.InterFrame;
import com.ttProject.frame.vp6.type.IntraFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * vp6のframe選択
 * @author taktod
 */
public class Vp6FrameSelector extends VideoSelector {
	/** 前回解析したkeyFrame情報は保持しておいて、interFrameに紐づける必要あり。 */
	private IntraFrame keyFrame = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		// はじめの1byteを読み込んでやりとりすることにします。
		if(channel.size() - channel.position() < 1) {
			// データがたりません。
			return null;
		}
		Bit1 frameMode = new Bit1();
		Bit6 qp = new Bit6();
		Bit1 marker = new Bit1();
		BitLoader loader = new BitLoader(channel);
		loader.load(frameMode, qp, marker);
		Vp6Frame frame = null;
		switch(frameMode.get()) {
		case 1: // interFrame
			frame = new InterFrame(frameMode, qp, marker);
			break;
		case 0: // intraFrame(keyFrame)
			frame = new IntraFrame(frameMode, qp, marker);
			keyFrame = (IntraFrame)frame;
			break;
		default:
			throw new Exception("解析不能なデータです。");
		}
		setup(frame);
		if(!(frame instanceof IntraFrame)) {
			frame.setKeyFrame(keyFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
