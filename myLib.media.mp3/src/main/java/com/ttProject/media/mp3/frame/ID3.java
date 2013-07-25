package com.ttProject.media.mp3.frame;

import java.nio.ByteBuffer;

import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public class ID3 extends Frame {
	public static final int tagSize = 10;
	private short version;
	private byte flg;
	public ID3(int position, int size, short version, byte flg) throws Exception {
		super(position, size);
		this.version = version;
		this.flg = flg;
	}
	public short getVersion() {
		return version;
	}
	public byte getFlg() {
		return flg;
	}
	@Override
	public void analyze(IReadChannel ch, IFrameAnalyzer analyzer)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ByteBuffer getBuffer() throws Exception {
		return null;
	}
}
