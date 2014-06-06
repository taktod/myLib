/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

/**
 * コーデック情報
 * いろいろあるけど、とりあえず一部だけいれとく。
 * @author taktod
 */
public enum CodecType {
	Unknown(0x0000),
	PCM(0x0001),
	MS_ADPCM(0x0002),
	IBM_CSVD(0x0005),
	A_LAW(0x0006),
	U_LAW(0x0007),
	OKI_ADPCM(0x0010),
	IMA_ADPCM(0x0011); // 今回の肝
	private final int value;
	private CodecType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static CodecType getCodec(int num) {
		for(CodecType type : values()) {
			if(type.intValue() == num) {
				return type;
			}
		}
		throw new RuntimeException("不明なコーデックです:" + num);
	}
}
