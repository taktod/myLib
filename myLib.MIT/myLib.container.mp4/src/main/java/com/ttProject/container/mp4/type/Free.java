package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * ftypの定義
 * @author taktod
 */
public class Free extends Mp4Atom {
	public Free(Bit32 size, Bit32 name) {
		super(size, name);
	}
	public Free() {
		super(new Bit32(), Type.getTypeBit(Type.Ftyp));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
