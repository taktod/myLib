package com.ttProject.media.h264.nal;

import com.ttProject.media.h264.INalAnalyzer;
import com.ttProject.media.h264.Nal;
import com.ttProject.nio.channels.IReadChannel;

public class AccessUnitDelimiter extends Nal {
	public AccessUnitDelimiter() {
		super(0, 0);
	}
	@Override
	public void analyze(IReadChannel ch, INalAnalyzer analyzer)
			throws Exception {
	}
}
