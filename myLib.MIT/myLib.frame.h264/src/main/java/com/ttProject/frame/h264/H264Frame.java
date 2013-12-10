package com.ttProject.frame.h264;

import java.nio.ByteBuffer;

import com.ttProject.frame.VideoFrame;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.BitConnector;

public abstract class H264Frame extends VideoFrame {
	private final Bit1 forbiddenZeroBit;
	private final Bit2 nalRefIdc;
	private final Bit5 type;
	public H264Frame(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		this.forbiddenZeroBit = forbiddenZeroBit;
		this.nalRefIdc = nalRefIdc;
		this.type = type;
	}
	protected ByteBuffer getTypeBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(forbiddenZeroBit,
				nalRefIdc, type);
	}
}
