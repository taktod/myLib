package com.ttProject.media.aac;

import com.ttProject.nio.channels.IReadChannel;

public class FrameAnalyzer implements IFrameAnalyzer {
	private final AacManager manager = new AacManager();
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		Frame frame = manager.getUnit(ch);
		if(frame == null) {
			return null;
		}
		frame.analyze(ch, this);
		ch.position(frame.getPosition() + frame.getSize());
		return frame;
	}
}
