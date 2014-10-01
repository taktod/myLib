/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.esds;

import com.ttProject.unit.extra.bit.Bit8;

public enum TagType {
	EsTag(0x03),
	DecoderConfig(0x04),
	DecoderSpecific(0x05),
	SlConfig(0x06);
	private final int value;
	private TagType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static TagType getType(int value) throws Exception {
		for(TagType t : values()) {
			if(t.intValue() == value) {
				return t;
			}
		}
		throw new Exception("unknwon tagType.:" + Integer.toHexString(value));
	}
	public static TagType getType(Bit8 tag) throws Exception {
		return getType(tag.get());
	}
}
