package com.ttProject.media.vp6.frame;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit6;
import com.ttProject.media.vp6.Frame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 内部フレーム
 * @author taktod
 */
public class InterFrame extends Frame {
	@SuppressWarnings("unused")
	private short offset; // 16bit
	public InterFrame(Bit1 frameMode, Bit6 qp, Bit1 marker) {
		super(frameMode, qp, marker);
	}
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
	}
}
