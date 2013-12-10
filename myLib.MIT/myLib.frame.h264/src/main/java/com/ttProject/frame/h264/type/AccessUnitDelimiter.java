package com.ttProject.frame.h264.type;

import com.ttProject.frame.h264.H264Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit5;

public class AccessUnitDelimiter extends H264Frame {
	public AccessUnitDelimiter(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
