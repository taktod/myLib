/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts;

import com.ttProject.frame.CodecType;

/**
 * codecInformation of mpegts
 * @author taktod
 */
public enum MpegtsCodecType {
	VIDEO_MPEG1(0x01, CodecType.UNKNOWN_VIDEO),
	VIDEO_MPEG2(0x02, CodecType.UNKNOWN_VIDEO),
	AUDIO_MPEG1(0x03, CodecType.MP3), // mp3とか
	AUDIO_MPEG2(0x04, CodecType.UNKNOWN_AUDIO),
	PRIVATE_SECTION(0x05, CodecType.NONE),
	PRIVATE_DATA(0x06, CodecType.NONE),
	AUDIO_AAC(0x0F, CodecType.AAC),
	VIDEO_MPEG4(0x10, CodecType.UNKNOWN_VIDEO),
	AUDIO_LATM_AAC(0x11, CodecType.UNKNOWN_VIDEO),
	SYSTEMS_MPEG4_PES(0x12, CodecType.NONE),
	SYSTEMS_MPEG4_SECTIONS(0x13, CodecType.NONE),
	VIDEO_H264(0x1B, CodecType.H264),
	AUDIO_AC3(0x81, CodecType.UNKNOWN_AUDIO),
	AUDIO_DTS(0x8A, CodecType.UNKNOWN_AUDIO),
	SUBTITLE_DVB(0x100, CodecType.NONE);
	private final int value;
	private final CodecType codecType;
	private MpegtsCodecType(int value, CodecType codecType) {
		this.value = value;
		this.codecType = codecType;
	}
	public int intValue() {
		return value;
	}
	public CodecType getCodecType() {
		return codecType;
	}
	public static MpegtsCodecType getType(int value) throws Exception {
		for(MpegtsCodecType t : values()) {
			if(t.intValue() == value) {
				return t;
			}
		}
		throw new Exception("unknown codec type is detected.:" + Integer.toHexString(value));
	}
}
