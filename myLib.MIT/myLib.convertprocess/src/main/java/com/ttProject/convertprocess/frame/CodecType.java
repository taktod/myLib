/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.frame;

/**
 * codecTypeの定義
 * @author taktod
 *
 */
public enum CodecType {
	AAC(0, true),
	ADPCM_IMA_WAV(1, true),
	ADPCM_SWF(2, true),
	MP3(6, true),
	NELLYMOSER(7, true),
	SPEEX(8, true),
	VORBIS(10, true),
	OPUS(15, true),
	FLV1(3, false),
	H264(4, false),
	MJPEG(5, false),
	THEORA(9, false),
	VP6(11, false),
	VP8(12, false),
	VP9(13, false),
	H265(14, false);
	private final int value;
	private final boolean audioFlg;
	/**
	 * コンストラクタ
	 * @param value
	 * @param audioFlg
	 */
	private CodecType(int value, boolean audioFlg) {
		this.value = value;
		this.audioFlg = audioFlg;
	};
	public boolean isAudio() {
		return audioFlg;
	}
	public int getValue() {
		return value;
	}
	/**
	 * 番号からコーデック値を応答する
	 * @param num
	 * @return
	 * @throws Exception
	 */
	public static CodecType getCodecType(int num) throws Exception {
		for(CodecType type : values()) {
			if(type.getValue() == num) {
				return type;
			}
		}
		throw new Exception("未対応のID番号です");
	}
}
