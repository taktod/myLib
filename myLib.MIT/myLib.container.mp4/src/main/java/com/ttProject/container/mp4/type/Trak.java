package com.ttProject.container.mp4.type;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * trakの定義
 * @author taktod
 */
public class Trak extends Mp4Atom {
	/** ロガー */
	private Logger logger = Logger.getLogger(Trak.class);
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Trak(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Trak() {
		super(new Bit32(), Type.getTypeBit(Type.Trak));
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
