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
 * CueRelativePosition
 * @author taktod
 */
public class CueRelativePosition extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public CueRelativePosition(EbmlValue size) {
		super(Type.CueRelativePosition, size);
	}
	/**
	 * constructor
	 */
	public CueRelativePosition() {
		this(new EbmlValue());
	}
}
