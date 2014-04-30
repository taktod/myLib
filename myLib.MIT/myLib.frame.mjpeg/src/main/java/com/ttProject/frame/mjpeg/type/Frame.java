package com.ttProject.frame.mjpeg.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.mjpeg.MjpegFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

public class Frame extends MjpegFrame {
	private ByteBuffer buffer = null;
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// ここでデータ実体を保持しておきたい。
		buffer = BufferUtil.safeRead(channel, channel.size());
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
