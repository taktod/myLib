package com.ttProject.frame.h264;

import java.nio.ByteBuffer;

import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;

public abstract class H264Frame extends VideoFrame {
	private final Bit1 forbiddenZeroBit;
	private final Bit2 nalRefIdc;
	private final Bit5 type;
	private SequenceParameterSet sps = null;
	private PictureParameterSet pps = null;
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
	public void setPps(PictureParameterSet pps) {
		this.pps = pps;
	}
	public void setSps(SequenceParameterSet sps) {
		this.sps = sps;
		if(sps != null) {
			setWidth(sps.getWidth());
			setHeight(sps.getHeight());
		}
	}
}
