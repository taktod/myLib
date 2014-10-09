/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

/**
 * riff unit type
 * @author taktod
 */
public enum Type {
	RIFF(0x52494646),
	FMT(0x666d7420),
	FACT(0x66616374),
	DATA(0x64617461),
	LIST(0x4C495354),
	hdrl(0x6864726C),
	avih(0x61766968),
	strl(0x7374726C),
	strh(0x73747268),
	strf(0x73747266);
	
	private final int value;
	private Type(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static Type getType(int num) {
		for(Type t : values()) {
			if(t.intValue() == num) {
				return t;
			}
		}
		throw new RuntimeException("unknown tag is found.:" + Integer.toHexString(num));
	}
}
