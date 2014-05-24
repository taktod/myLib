/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CueClusterPositionタグ
 * @author taktod
 */
public class CueClusterPosition extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CueClusterPosition(EbmlValue size) {
		super(Type.CueClusterPosition, size);
	}
	/**
	 * コンストラクタ
	 */
	public CueClusterPosition() {
		this(new EbmlValue());
	}
}
