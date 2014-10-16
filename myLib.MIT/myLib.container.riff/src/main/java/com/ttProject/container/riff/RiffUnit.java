/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import com.ttProject.container.Container;

/**
 * riff unit
 * @author taktod
 */
public abstract class RiffUnit extends Container {
	private final Type fcc; // fourcc
	/**
	 * constructor
	 * @param type
	 */
	public RiffUnit(Type type) {
		fcc = type;
		super.setSize(4);
	}
	/**
	 * ref the fourCC
	 * @return
	 */
	public Type getFourCC() {
		return fcc;
	}
}
