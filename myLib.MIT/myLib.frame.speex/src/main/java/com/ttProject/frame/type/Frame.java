package com.ttProject.frame.type;

import com.ttProject.frame.SpeexFrame;
import com.ttProject.nio.channels.IReadChannel;

public class Frame extends SpeexFrame {
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setReadPosition(channel.position());
		super.setSize(channel.size());
		super.setSampleNum(320);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
