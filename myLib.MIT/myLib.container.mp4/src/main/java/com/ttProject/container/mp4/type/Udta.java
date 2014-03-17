package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4ParentAtom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * udtaの定義
 * @see http://svn.python.org/projects/python/branches/py3k-importhook/Lib/plat-mac/Carbon/QuickTime.py
 * しらないタグがあるっぽいので調査が必要
 * @author taktod
 */
public class Udta extends Mp4ParentAtom {
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Udta(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Udta() {
		super(new Bit32(), Type.getTypeBit(Type.Udta));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
