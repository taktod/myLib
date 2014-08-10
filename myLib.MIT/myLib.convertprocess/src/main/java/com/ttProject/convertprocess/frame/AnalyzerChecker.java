/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.frame;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.aac.AacFrameAnalyzer;
import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrameAnalyzer;
import com.ttProject.frame.adpcmswf.AdpcmswfFrameAnalyzer;
import com.ttProject.frame.flv1.Flv1FrameAnalyzer;
import com.ttProject.frame.h264.NalAnalyzer;
import com.ttProject.frame.mjpeg.MjpegFrameAnalyzer;
import com.ttProject.frame.mp3.Mp3FrameAnalyzer;
import com.ttProject.frame.nellymoser.NellymoserFrameAnalyzer;
import com.ttProject.frame.opus.OpusFrameAnalyzer;
import com.ttProject.frame.speex.SpeexFrameAnalyzer;
import com.ttProject.frame.theora.TheoraFrameAnalyzer;
import com.ttProject.frame.vorbis.VorbisFrameAnalyzer;
import com.ttProject.frame.vp6.Vp6FrameAnalyzer;
import com.ttProject.frame.vp8.Vp8FrameAnalyzer;
import com.ttProject.frame.vp9.Vp9FrameAnalyzer;

/**
 * frameを復元するときのanalyzerを確認する
 * @author taktod
 */
public class AnalyzerChecker {
	/**
	 * 解析用のanalyzerがどれであるか、調べる
	 */
	public IAnalyzer checkAnalyzer(CodecType codecType) {
		switch(codecType) {
		case AAC:
			return new AacFrameAnalyzer();
		case ADPCM_IMA_WAV:
			return new AdpcmImaWavFrameAnalyzer();
		case ADPCM_SWF:
			return new AdpcmswfFrameAnalyzer();
		case FLV1:
			return new Flv1FrameAnalyzer();
		case H264:
			return new NalAnalyzer();
		case H265:
			throw new RuntimeException("あとで考えます。こっちもH265NalAnalyzerを使うと思う。");
		case MJPEG:
			return new MjpegFrameAnalyzer();
		case MP3:
			return new Mp3FrameAnalyzer();
		case NELLYMOSER:
			return new NellymoserFrameAnalyzer();
		case OPUS:
			return new OpusFrameAnalyzer();
		case SPEEX:
			return new SpeexFrameAnalyzer();
		case THEORA:
			return new TheoraFrameAnalyzer();
		case VORBIS:
			return new VorbisFrameAnalyzer();
		case VP6:
			return new Vp6FrameAnalyzer();
		case VP8:
			return new Vp8FrameAnalyzer();
		case VP9:
			return new Vp9FrameAnalyzer();
		default:
			throw new RuntimeException("未定義のcodecTypeです");
		}
	}
}
