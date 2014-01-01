package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * nmhdの定義
 * @author taktod
 */
public class Nmhd extends Mp4Atom {
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Nmhd(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Nmhd() {
		super(new Bit32(), Type.getTypeBit(Type.Nmhd));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
