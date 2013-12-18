package com.ttProject.frame.adpcmswf;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.adpcmswf.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

public class AdpcmswfFrameSelector extends AudioSelector {
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		AdpcmswfFrame frame = new Frame();
		frame.minimumLoad(channel);
		return frame;
	}
}
