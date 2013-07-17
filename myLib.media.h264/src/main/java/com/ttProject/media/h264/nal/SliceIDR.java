package com.ttProject.media.h264.nal;

import com.ttProject.media.h264.INalAnalyzer;
import com.ttProject.media.h264.Nal;
import com.ttProject.nio.channels.IReadChannel;

/**
 * sliceIDR(keyFrame)
 * @author taktod
 *
 */
public class SliceIDR extends Nal {
	public SliceIDR() {
		super(0, 0);
	}
	@Override
	public void analyze(IReadChannel ch, INalAnalyzer analyzer)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
}
