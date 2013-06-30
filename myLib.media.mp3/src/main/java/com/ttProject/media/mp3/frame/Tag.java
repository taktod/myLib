package com.ttProject.media.mp3.frame;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.mp3.Frame;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mp3のid3v1タグ、あとでサポートしておきたい。
 * @author taktod
 */
public class Tag extends Frame {
	public Tag() {
		super(0, 0);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
	}

}
