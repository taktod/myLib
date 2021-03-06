/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

/**
 * codecType definition
 * @author taktod
 */
public enum CodecType {
	AAC(0,           Type.AUDIO),
	ADPCM_IMA_WAV(1, Type.AUDIO),
	ADPCM_SWF(2,     Type.AUDIO),
	MP3(6,           Type.AUDIO),
	NELLYMOSER(7,    Type.AUDIO),
	PCM_ALAW(16,     Type.AUDIO),
	PCM_MULAW(17,    Type.AUDIO),
	SPEEX(8,         Type.AUDIO),
	VORBIS(10,       Type.AUDIO),
	OPUS(15,         Type.AUDIO),
	FLV1(3,          Type.VIDEO),
	H264(4,          Type.VIDEO),
	MJPEG(5,         Type.VIDEO),
	THEORA(9,        Type.VIDEO),
	VP6(11,          Type.VIDEO),
	VP8(12,          Type.VIDEO),
	VP9(13,          Type.VIDEO),
	H265(14,         Type.VIDEO),
	NONE(-1,         Type.OTHER),
	UNKNOWN_AUDIO(-2,Type.AUDIO),
	UNKNOWN_VIDEO(-3,Type.VIDEO);
	private static enum Type {
		AUDIO,
		VIDEO,
		OTHER
	};
	private final int id;
	private final Type type;
	/**
	 * constructor
	 * @param value
	 * @param audioFlg
	 */
	private CodecType(int id, Type type) {
		this.id = id;
		this.type = type;
	};
	/**
	 * is audio data?
	 * @return
	 */
	public boolean isAudio() {
		return type == Type.AUDIO;
	}
	/**
	 * is video data?
	 * @return
	 */
	public boolean isVideo() {
		return type == Type.VIDEO;
	}
	/**
	 * ref the id(id for myLib.)
	 * @return
	 */
	public int getId() {
		return id;
	}
	/**
	 * get the codecType from id
	 * @param num
	 * @return
	 * @throws Exception
	 */
	public static CodecType getCodecType(int num) throws Exception {
		for(CodecType type : values()) {
			if(type.getId() == num) {
				return type;
			}
		}
		throw new Exception("invalid codecId is found.:" + num);
	}
}
