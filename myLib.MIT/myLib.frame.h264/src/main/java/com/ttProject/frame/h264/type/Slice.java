package com.ttProject.frame.h264.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.h264.H264Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.util.BufferUtil;

public class Slice extends H264Frame {
	private ByteBuffer buffer = null;
	public Slice(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setReadPosition(channel.position());
		setSize(channel.size());
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("データ実体が読み込まれていません");
		}
		setData(BufferUtil.connect(getTypeBuffer(),
				buffer));
	}
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
}
