/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.header;

/**
 * HeaderType
 * @author taktod
 */
public enum HeaderType {
	Type0(0),
	Type1(1),
	Type2(2),
	Type3(3);
	private final int value;
	private HeaderType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static HeaderType getType(final int value) {
		for(HeaderType t : values()) {
			if(t.intValue() == value) {
				return t;
			}
		}
		throw new RuntimeException("out of range:" + value);
	}
}
