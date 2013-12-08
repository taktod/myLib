package com.ttProject.frame.vp6.type;

import com.ttProject.frame.vp6.Vp6Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit6;

public class InterFrame extends Vp6Frame {
	public InterFrame(Bit1 frameMode, Bit6 qp, Bit1 marker) {
		super(frameMode, qp, marker);
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
