package com.ttProject.container.mp4;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * mp4Atomのベースになるクラス
 * @author taktod
 */
public abstract class Mp4ParentAtom extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Mp4ParentAtom.class);
	/**
	 * コンストラクタ
	 * @param length
	 * @param name
	 */
	public Mp4ParentAtom(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		int targetSize = getSize() - 8;
		IContainer container = null;
		while(targetSize > 0 && (container = getMp4AtomReader().read(channel)) != null) {
			targetSize -= container.getSize();
		}
		super.load(channel);
	}
}
