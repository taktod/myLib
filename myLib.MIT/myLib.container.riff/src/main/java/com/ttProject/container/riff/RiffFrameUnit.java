/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * riffFrameUnit
 * base for frame unit.
 * @author taktod
 */
public abstract class RiffFrameUnit extends RiffSizeUnit {
	/** to hold the passedUnitCount for pts. */
	private static Map<Integer, Integer> trackCountMap = new ConcurrentHashMap<Integer, Integer>();
	private final int trackId;
	/**
	 * constructor
	 * @param typeValue
	 * @param type
	 */
	public RiffFrameUnit(int dataValue, Type type) {
		super(type);
		byte[] dat = new byte[2];
		dat[0] = (byte)((dataValue >> 24) & 0xFF);
		dat[1] = (byte)((dataValue >> 16) & 0xFF);
		trackId = Integer.parseInt(new String(dat).intern());
	}
}
