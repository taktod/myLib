/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mpegts;

/**
 * mpegtsの内部にあるコーデック情報
 * @author taktod
 */
public enum CodecType {
	VIDEO_MPEG1(0x01),
	VIDEO_MPEG2(0x02),
	AUDIO_MPEG1(0x03), // mp3とか
	AUDIO_MPEG2(0x04),
	PRIVATE_SECTION(0x05),
	PRIVATE_DATA(0x06),
	AUDIO_AAC(0x0F),
	VIDEO_MPEG4(0x10),
	AUDIO_LATM_AAC(0x11),
	SYSTEMS_MPEG4_PES(0x12),
	SYSTEMS_MPEG4_SECTIONS(0x13),
	VIDEO_H264(0x1B),
	AUDIO_AC3(0x81),
	AUDIO_DTS(0x8A),
	SUBTITLE_DVB(0x100);
	private final int value;
	private CodecType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static CodecType getType(int value) throws Exception {
		for(CodecType t : values()) {
			if(t.intValue() == value) {
				return t;
			}
		}
		throw new Exception("知らないコーデックタイプを検知しました。:" + Integer.toHexString(value));
	}
}
