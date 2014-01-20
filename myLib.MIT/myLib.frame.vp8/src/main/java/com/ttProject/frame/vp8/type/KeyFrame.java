package com.ttProject.frame.vp8.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.vp8.Vp8Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit14;
import com.ttProject.unit.extra.bit.Bit19;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;

public class KeyFrame extends Vp8Frame {
	public static byte[] startCode = {(byte)0x9D, (byte)0x01, (byte)0x2A};
	private Bit2 horizontalScale;
	private Bit14 width;
	private Bit2 verticalScale;
	private Bit14 height;
	public KeyFrame(Bit1 frameType, Bit3 version, Bit1 showFrame, Bit19 firstPartSize) {
		super(frameType, version, showFrame, firstPartSize);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// ここでサイズのデータを読み込みたい

	}

	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
}
