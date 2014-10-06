/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import com.ttProject.frame.CodecType;

/**
 * mkvCodecType
 * @author taktod
 */
public enum MkvCodecType {
	V_MPEG4_ISO_AVC("V_MPEG4/ISO/AVC", CodecType.H264),
	V_MPEG_ISO_HEVC("V_MPEG/ISO/HEVC", CodecType.H265),
	V_VP8("V_VP8", CodecType.VP8),
	V_VP9("V_VP9", CodecType.VP9),
	V_MJPEG("V_MJPEG", CodecType.MJPEG),

//	V_MS("V_MS/VFW/FOURCC"), // microsoft mpeg4 v2? same as A_MS/ACM?
	V_THEORA("V_THEORA", CodecType.THEORA), // theora
	A_AAC("A_AAC", CodecType.AAC),
	A_MPEG_L3("A_MPEG/L3", CodecType.MP3),
	A_VORBIS("A_VORBIS", CodecType.VORBIS),
	A_OPUS("A_OPUS", CodecType.OPUS),
	A_MS_ACM("A_MS/ACM", CodecType.UNKNOWN_AUDIO), // could have FMT for riff, FMT will decide the codecs.
//	S_TEXT_UTF8("S_TEXT/UTF8"), // subtitle
/*	D_WEBVTT_SUBTITLES("D_WEBVTT/SUBTITLES"), // webVtt
	D_WEBVTT_CAPTIONS("D_WEBVTT/CAPTIONS"),
	D_WEBVTT_DESCRIPTIONS("D_WEBVTT/DESCRIPTIONS"),
	D_WEBVTT_METADATA("D_WEBVTT/METADATA"),*/
	;
	private final String name;
	private final CodecType codecType;
	private MkvCodecType(String name, CodecType codecType) {
		this.name = name;
		this.codecType = codecType;
	}
	public CodecType getCodecType() {
		return codecType;
	}
	@Override
	public String toString() {
		return name;
	}
	/**
	 * get mkvCodecType from CodecType
	 * @param codecType
	 * @return
	 * @throws Exception
	 */
	public static MkvCodecType getCodecType(CodecType codecType) throws Exception {
		for(MkvCodecType type : values()) {
			if(type.getCodecType() == codecType) {
				return type;
			}
		}
		throw new RuntimeException("mkvCodecType is not decided.:" + codecType);
	}
	/**
	 * get MkvCodecType from CodecID string.
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static MkvCodecType getMkvCodecType(String data) throws Exception {
		if(data.startsWith("V_MPEG")) {
			if(data.contains("AVC")) {
				return V_MPEG4_ISO_AVC;
			}
			if(data.contains("HEVC")) {
				return V_MPEG_ISO_HEVC;
			}
		}
		else if(data.startsWith("V_VP8")) {
			return V_VP8;
		}
		else if(data.startsWith("V_VP9")) {
			return V_VP9;
		}
		else if(data.startsWith("A_AAC")) {
			return A_AAC;
		}
		else if(data.startsWith("A_MPEG")) {
			if(data.contains("L3")) {
				return A_MPEG_L3;
			}
		}
		else if(data.startsWith("A_VORBIS")) {
			return A_VORBIS;
		}
		else if(data.startsWith("V_MJPEG")) {
			return V_MJPEG;
		}
		else if(data.startsWith("A_MS")) {
			if(data.contains("ACM")) {
				return A_MS_ACM;
			}
		}
/*		else if(data.startsWith("V_MS")) {
			return V_MS;
		}*/
		else if(data.startsWith("V_THEORA")) {
			return V_THEORA;
		}
/*		else if(data.startsWith("S_TEXT")) {
			return S_TEXT;
		}*/
		throw new Exception("unexpected codec type string. I need a sample.:" + data);
	}
}
