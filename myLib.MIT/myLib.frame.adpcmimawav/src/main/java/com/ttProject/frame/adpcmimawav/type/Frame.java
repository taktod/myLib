package com.ttProject.frame.adpcmimawav.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

public class Frame extends AdpcmImaWavFrame {
	private ByteBuffer buffer = null;
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, channel.size());
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		setData(buffer);
	}
}
