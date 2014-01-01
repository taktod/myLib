package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * stssの定義
 * @author taktod
 */
public class Stss extends Mp4Atom {
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Stss(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Stss() {
		super(new Bit32(), Type.getTypeBit(Type.Stss));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
