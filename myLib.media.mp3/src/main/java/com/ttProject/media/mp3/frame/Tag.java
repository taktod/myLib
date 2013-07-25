package com.ttProject.media.mp3.frame;

import java.nio.ByteBuffer;

import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mp3のid3v1タグ、あとでサポートしておきたい。
 * @see http://mpgedit.org/mpgedit/mpeg_format/mpeghdr.htm
 * 内容
 * 先頭[TAG]
 * 30バイト title
 * 30バイト artist
 * 30バイト album
 * 4バイト year
 * 30バイト comment
 * 1バイト ジャンル
 * @author taktod
 */
public class Tag extends Frame {
	public Tag() {
		super(0, 0);
	}
	@Override
	public void analyze(IReadChannel ch, IFrameAnalyzer analyzer)
			throws Exception {
	}
	@Override
	public ByteBuffer getBuffer() throws Exception {
		return null;
	}
}
