/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264;

/**
 * def of h264 slice type.
 * @author taktod
 */
public enum SliceType {
	P_slice(0),
	B_slice(1),
	I_slice(2),
	SP_slice(3),
	SI_slice(4),
	P_slice2(5),
	B_slice2(6),
	I_slice2(7),
	SP_slice2(8),
	SI_slice2(9);
	private final int value;
	private SliceType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static SliceType getType(int value) throws Exception {
		for(SliceType t : values()) {
			if(t.intValue() == value) {
				return t;
			}
		}
		throw new Exception("unexpected value:" + value);
	}
}
