package com.ttProject.container.mp4.stsd.record;

import java.nio.ByteBuffer;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.util.BufferUtil;

/**
 * 全データがh264のconfigDataになっています。
 * @author taktod
 */
public class Avcc extends Mp4Atom {
	private ByteBuffer buffer = null;
	public Avcc(Bit32 size, Bit32 name) {
		super(size, name);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, getSize() - 8);
		super.load(channel);
	}
	/**
	 * 内容データ参照(spsとppsにしてもいいかもしれない。)
	 * @return
	 */
	public ByteBuffer getBuffer() {
		return buffer.duplicate();
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
