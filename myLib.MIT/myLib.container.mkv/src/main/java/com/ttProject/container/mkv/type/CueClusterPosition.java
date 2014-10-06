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
 * CueClusterPosition
 * @author taktod
 */
public class CueClusterPosition extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public CueClusterPosition(EbmlValue size) {
		super(Type.CueClusterPosition, size);
	}
	/**
	 * constructor
	 */
	public CueClusterPosition() {
		this(new EbmlValue());
	}
}
