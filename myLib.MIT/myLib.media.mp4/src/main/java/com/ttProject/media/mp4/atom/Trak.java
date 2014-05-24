/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Trak extends ParentAtom {
	public Trak(int position, int size) {
		super(Trak.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
