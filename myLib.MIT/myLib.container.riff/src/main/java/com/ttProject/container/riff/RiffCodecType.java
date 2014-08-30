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
public enum RiffCodecType {
	Unknown(0x0000),
	PCM(0x0001),
	MS_ADPCM(0x0002),
	IBM_CSVD(0x0005),
	A_LAW(0x0006),
	U_LAW(0x0007),
	OKI_ADPCM(0x0010),
	IMA_ADPCM(0x0011), // 今回の肝
	MP3(0x0055);
	private final int value;
	private RiffCodecType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static RiffCodecType getCodec(int num) {
		for(RiffCodecType type : values()) {
			if(type.intValue() == num) {
				return type;
			}
		}
		throw new RuntimeException("不明なコーデックです:" + num);
	}
}
