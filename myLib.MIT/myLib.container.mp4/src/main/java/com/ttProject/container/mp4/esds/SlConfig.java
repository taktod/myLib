package com.ttProject.container.mp4.esds;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

public class SlConfig extends Tag {
	/** ロガー */
	private Logger logger = Logger.getLogger(SlConfig.class);
	private ByteBuffer data = null;
	public SlConfig(Bit8 tag) {
		super(tag);
	}
	public SlConfig() {
		super(new Bit8(0x06));
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		data = BufferUtil.safeRead(channel, getSize());
		logger.info(HexUtil.toHex(data, true));
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
