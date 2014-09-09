/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.vp6;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit6;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.vp6.frame.InterFrame;
import com.ttProject.media.vp6.frame.IntraFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * frameを解析する動作
 * @author taktod
 */
public class FrameAnalyzer implements IFrameAnalyzer {
	private IntraFrame lastKeyFrame = null;
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		BitLoader bitLoader = new BitLoader(ch);
		Bit1 frameMode = new Bit1();
		Bit6 qp = new Bit6();
		Bit1 marker = new Bit1();
		bitLoader.load(frameMode, qp, marker);
		Frame frame = null;
		switch(frameMode.get()) {
		case 0: // intraFrame
			frame = new IntraFrame(frameMode, qp, marker);
			lastKeyFrame = (IntraFrame) frame;
			break;
		case 1: // interFrame
			frame = new InterFrame(frameMode, qp, marker);
			frame.setLastKeyFrame(lastKeyFrame);
			break;
		default:
			throw new Exception("frameTypeがおかしい値でした。");
		}
		frame.analyze(ch, null);
		return frame;
	}
}
