package com.ttProject.container.riff;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * riff size unit.
 * riff unit which contains size information.
 * @author taktod
 */
public abstract class RiffSizeUnit extends RiffUnit {
	private Bit32 cb = new Bit32(); // size information except fourcc and cb. (-8byte)
	/**
	 * constructor
	 * @param type
	 */
	public RiffSizeUnit(Type type) {
		super(type);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(cb);
		super.setSize(cb.get() + 8);
	}
}
