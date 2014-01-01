package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4ParentAtom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * mfraの定義
 * @author taktod
 */
public class Mfra extends Mp4ParentAtom {
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Mfra(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Mfra() {
		super(new Bit32(), Type.getTypeBit(Type.Mfra));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
