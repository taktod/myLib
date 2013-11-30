package com.ttProject.media.vp6;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit6;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.vp6.frame.InterFrame;
import com.ttProject.media.vp6.frame.IntraFrame;
import com.ttProject.nio.channels.IReadChannel;

public class FrameAnalyzer implements IFrameAnalyzer {
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
			break;
		case 1: // interFrame
			frame = new InterFrame(frameMode, qp, marker);
			break;
		default:
			throw new Exception("frameTypeがおかしい値でした。");
		}
		frame.analyze(ch, null);
		return frame;
	}
}
