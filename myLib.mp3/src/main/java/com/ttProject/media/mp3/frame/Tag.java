package com.ttProject.media.mp3.frame;

import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mp3のid3v1タグ
 * とりあえず最後にくるタグなので、きちんと調査してない＾＾；
 * @author taktod
 */
public class Tag extends Frame {
	public Tag(byte[] entry) {
		super(0, 0);
	}
	@Override
	public void analyze(IReadChannel ch, IFrameAnalyzer analyzer)
			throws Exception {
		
	}
}
