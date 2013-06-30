package com.ttProject.media.mp3.frame;

import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mp3のid3v2タグ
 * 先頭に洗われる可能性のあるタグ
 * @author taktod
 */
public class ID3 extends Frame {
	public static final int tagSize = 10;
	private short version;
	private byte flg;
	public ID3(int position, int size, short version, byte flg) throws Exception {
		super(position, size);
		this.version = version;
		this.flg = flg;
	}
	@Override
	public void analyze(IFileReadChannel ch, IFrameAnalyzer analyzer)
			throws Exception {
	}
	public short getVersion() {
		return version;
	}
	public byte getFlg() {
		return flg;
	}
}
