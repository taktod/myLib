/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex.sub;

import java.util.List;

import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;

/**
 * abstract for NarrowUnit or WideUnit
 * @author taktod
 */
public abstract class SubUnit {
	public abstract void load(BitLoader loader) throws Exception;
	public abstract int getBitCount();
	public abstract List<Bit> getBitList();
}
