package com.ttProject.media.mp3.frame;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.mp3.Frame;
import com.ttProject.nio.channels.IFileReadChannel;

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
	public void analyze(IFileReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
	}
	public short getVersion() {
		return version;
	}
	public byte getFlg() {
		return flg;
	}
}
