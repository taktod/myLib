package com.ttProject.container.mp4.type;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * ftypの定義
 * @author taktod
 */
public class Moov extends Mp4Atom {
	/** ロガー */
	private Logger logger = Logger.getLogger(Moov.class);
	public Moov(Bit32 size, Bit32 name) {
		super(size, name);
	}
	public Moov() {
		super(new Bit32(), Type.getTypeBit(Type.Ftyp));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		IContainer container = null;
		while((container = getMp4AtomReader().read(channel)) != null) {
			logger.info(container);
		}
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
