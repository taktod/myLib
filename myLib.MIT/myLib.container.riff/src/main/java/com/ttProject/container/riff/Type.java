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
	strf(0x73747266),
	INFO(0x494E464F),
	ISFT(0x49534654),
	movi(0x6d6f7669),
	db(0x00006462),
	dc(0x00006463),
	pc(0x00007063),
	wb(0x00007762),
	idx1(0x69647831),
	JUNK(0x4A554E4B);
	
	private final int value;
	private Type(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static Type getType(int num) {
		for(Type t : values()) {
			switch(t) {
			case db:
			case dc:
			case pc:
			case wb:
				if(t.intValue() == (num & 0x0000FFFF)) {
					return t;
				}
				break;
			default:
				if(t.intValue() == num) {
					return t;
				}
			}
		}
		throw new RuntimeException("unknown tag is found.:" + Integer.toHexString(num));
	}
}
