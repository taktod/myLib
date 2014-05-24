/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.stsd;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.unit.extra.bit.Bit32;

public abstract class DescriptionRecord extends Mp4Atom {
	public DescriptionRecord(Bit32 size, Bit32 name) {
		super(size, name);
	}
}
