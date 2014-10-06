/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

/**
 * enum for lacing
 * @author taktod
 */
public enum Lacing {
	No(0),
	Xiph(1),
	EBML(3),
	FixedSize(2);
	private int value;
	private Lacing(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static Lacing getType(int value) throws Exception {
		for(Lacing t : values()) {
			if(t.value == value) {
				return t;
			}
		}
		throw new Exception("lacing type is undefined.:" + value);
	}
}
