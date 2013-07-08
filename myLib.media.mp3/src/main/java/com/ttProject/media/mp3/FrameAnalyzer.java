package com.ttProject.media.mp3;

import com.ttProject.media.mp3.frame.Mp3;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mp3の解析動作
 * @author taktod
 */
public class FrameAnalyzer implements IFrameAnalyzer {
	private int frameCount = 0;
	public void clearFrameCount() {
		frameCount = 0;
	}
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		Frame frame = Frame.getFrame(ch, frameCount);
		if(frame == null) {
			return null;
		}
		frame.analyze(ch, this);
		if(frame instanceof Mp3) {
			frameCount ++;
		}
		ch.position(frame.getPosition() + frame.getSize());
		return frame;
	}
}
