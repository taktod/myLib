package com.ttProject.container.mp4;

import com.ttProject.container.IContainer;
import com.ttProject.container.Reader;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mp4のatom構造を読み込むreader
 * @author taktod
 */
public class Mp4AtomReader extends Reader {
	/**
	 * コンストラクタ
	 */
	public Mp4AtomReader() {
		super(new Mp4AtomSelector());
	}
	@Override
	public IContainer read(IReadChannel channel) throws Exception {
		Mp4Atom container = (Mp4Atom)getSelector().select(channel);
		if(container != null) {
			container.setMp4AtomReader(this);
			container.load(channel);
		}
		return container;
	}
}
