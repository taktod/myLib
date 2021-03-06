/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * skipの定義
 * @author taktod
 */
public class Skip extends Mp4Atom {
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Skip(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Skip() {
		super(new Bit32(), Type.getTypeBit(Type.Skip));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
