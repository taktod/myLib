package com.ttProject.frame;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;

/**
 * 空のフレーム
 * @author taktod
 */
public class NullFrame extends Frame {
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	@Override
	public float getDuration() {
		return 0;
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
