package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * ftypの定義
 * @author taktod
 */
public class Mvhd extends Mp4Atom {
	public Mvhd(Bit32 size, Bit32 name) {
		super(size, name);
	}
	public Mvhd() {
		super(new Bit32(), Type.getTypeBit(Type.Mvhd));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
