package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * ftypの定義
 * @author taktod
 */
public class Hdlr extends Mp4Atom {
	public Hdlr(Bit32 size, Bit32 name) {
		super(size, name);
	}
	public Hdlr() {
		super(new Bit32(), Type.getTypeBit(Type.Ftyp));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
