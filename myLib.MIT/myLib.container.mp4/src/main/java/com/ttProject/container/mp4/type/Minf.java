package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4ParentAtom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * minfの定義
 * @author taktod
 */
public class Minf extends Mp4ParentAtom {
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Minf(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Minf() {
		super(new Bit32(), Type.getTypeBit(Type.Minf));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
