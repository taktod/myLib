/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU AFFERO GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.humble.frameutil;

import io.humble.video.Codec;

import com.ttProject.frame.CodecType;

/**
 * codecTypeがなにであるか判定する
 * @author taktod
 */
public class CodecTypeChecker {
	/**
	 * codecTypeを調べる
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CodecType getCodecType(Codec.ID id) throws Exception {
		switch(id) {
		case CODEC_ID_AAC:
			return CodecType.AAC;
		case CODEC_ID_ADPCM_IMA_WAV:
			return CodecType.ADPCM_IMA_WAV;
		case CODEC_ID_ADPCM_SWF:
			return CodecType.ADPCM_SWF;
		case CODEC_ID_FLV1:
			return CodecType.FLV1;
		case CODEC_ID_H264:
			return CodecType.H264;
		case CODEC_ID_MJPEG:
			return CodecType.MJPEG;
		case CODEC_ID_MP3:
			return CodecType.MP3;
		case CODEC_ID_NELLYMOSER:
			return CodecType.NELLYMOSER;
		case CODEC_ID_OPUS:
			return CodecType.OPUS;
		case CODEC_ID_PCM_ALAW:
			return CodecType.PCM_ALAW;
		case CODEC_ID_PCM_MULAW:
			return CodecType.PCM_MULAW;
		case CODEC_ID_SPEEX:
			return CodecType.SPEEX;
		case CODEC_ID_THEORA:
			return CodecType.THEORA;
		case CODEC_ID_VORBIS:
			return CodecType.VORBIS;
		case CODEC_ID_VP6F:
			return CodecType.VP6;
		case CODEC_ID_VP8:
			return CodecType.VP8;
		case CODEC_ID_VP9:
			return CodecType.VP9;
		default:
			throw new Exception("未定義のcodecTypeです");
		}
	}
}
