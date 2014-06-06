/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

/**
 * riffのunitタイプ
 * @author taktod
 */
public enum Type {
	RIFF(0x52494646),
	FMT(0x666d7420),
	FACT(0x66616374),
	DATA(0x64617461),
	LIST(0x4C495354);
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
		throw new RuntimeException("不明なタグを発見しました。:" + Integer.toHexString(num));
	}
}
