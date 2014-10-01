/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import com.ttProject.frame.CodecType;

/**
 * コーデック情報
 * いろいろあるけど、とりあえず一部だけいれとく。
 * @author taktod
 */
public enum RiffCodecType {
	Unknown(0x0000, CodecType.NONE),
	PCM(0x0001, CodecType.UNKNOWN_AUDIO),
	MS_ADPCM(0x0002, CodecType.UNKNOWN_AUDIO),
	IBM_CSVD(0x0005, CodecType.UNKNOWN_AUDIO),
	A_LAW(0x0006, CodecType.PCM_ALAW),
	U_LAW(0x0007, CodecType.PCM_MULAW),
	OKI_ADPCM(0x0010, CodecType.UNKNOWN_AUDIO),
	IMA_ADPCM(0x0011, CodecType.ADPCM_IMA_WAV), // 今回の肝
	MP3(0x0055, CodecType.MP3);
	private final int value;
	private final CodecType codecType;
	private RiffCodecType(int value, CodecType codecType) {
		this.value = value;
		this.codecType = codecType;
	}
	public int intValue() {
		return value;
	}
	public CodecType getCodecType() {
		return codecType;
	}
	public static RiffCodecType getCodec(int num) {
		for(RiffCodecType type : values()) {
			if(type.intValue() == num) {
				return type;
			}
		}
		throw new RuntimeException("codecId is unknown.:" + num);
	}
}
