package com.ttProject.frame.vp8;

import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit19;
import com.ttProject.unit.extra.bit.Bit3;

public abstract class Vp8Frame extends VideoFrame {
	private final Bit1  frameType; // 0ならkyeFrame
	private final Bit3  version;
	private final Bit1  showFrame;
	private final Bit19 firstPartSize;
	public Vp8Frame(Bit1 frameType, Bit3 version, Bit1 showFrame, Bit19 firstPartSize) {
		this.frameType     = frameType;
		this.version       = version;
		this.showFrame     = showFrame;
		this.firstPartSize = firstPartSize;
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

}
