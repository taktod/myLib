package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4ParentAtom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * mdiaの定義
 * @author taktod
 */
public class Mdia extends Mp4ParentAtom {
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Mdia(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Mdia() {
		super(new Bit32(), Type.getTypeBit(Type.Mdia));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
