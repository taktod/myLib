package com.ttProject.container.riff.type;

import java.nio.ByteBuffer;

import com.ttProject.container.riff.RiffSizeUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * Junk
 * @author taktod
 */
public class Junk extends RiffSizeUnit {
	private ByteBuffer junkBuffer = null;
	/**
	 * constructor
	 */
	public Junk() {
		super(Type.JUNK);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		junkBuffer = BufferUtil.safeRead(channel, getSize()-8);
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
