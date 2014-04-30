package com.ttProject.frame.adpcmimawav;

import java.nio.ByteBuffer;

import com.ttProject.frame.AudioFrame;
import com.ttProject.nio.channels.IReadChannel;

public class AdpcmImaWavFrame extends AudioFrame {
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
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
