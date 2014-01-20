package com.ttProject.frame.vp8.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.vp8.Vp8Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit19;
import com.ttProject.unit.extra.bit.Bit3;

public class IntraFrame extends Vp8Frame {
	public IntraFrame(Bit1 frameType, Bit3 version, Bit1 showFrame, Bit19 firstPartSize) {
		super(frameType, version, showFrame, firstPartSize);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
