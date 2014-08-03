/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.frame;

import com.ttProject.frame.IFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrame;
import com.ttProject.frame.adpcmswf.AdpcmswfFrame;
import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h265.H265Frame;
import com.ttProject.frame.mjpeg.MjpegFrame;
import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.frame.nellymoser.NellymoserFrame;
import com.ttProject.frame.opus.OpusFrame;
import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.frame.theora.TheoraFrame;
import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.frame.vp6.Vp6Frame;
import com.ttProject.frame.vp8.Vp8Frame;
import com.ttProject.frame.vp9.Vp9Frame;

/**
 * frameを送信するときのcodecTypeを確認する
 * @author taktod
 */
public class CodecChecker {
	/**
	 * 対応codecTypeを調べる
	 * @param frame
	 * @return
	 */
	public CodecType checkCodecType(IFrame frame) {
		if(frame instanceof AacFrame) {
			return CodecType.AAC;
		}
		else if(frame instanceof AdpcmImaWavFrame) {
			return CodecType.ADPCM_IMA_WAV;
		}
		else if(frame instanceof AdpcmswfFrame) {
			return CodecType.ADPCM_SWF;
		}
		else if(frame instanceof Flv1Frame) {
			return CodecType.FLV1;
		}
		else if(frame instanceof H264Frame) {
			return CodecType.H264;
		}
		else if(frame instanceof H265Frame) {
			return CodecType.H265;
		}
		else if(frame instanceof MjpegFrame) {
			return CodecType.MJPEG;
		}
		else if(frame instanceof Mp3Frame) {
			return CodecType.MP3;
		}
		else if(frame instanceof NellymoserFrame) {
			return CodecType.NELLYMOSER;
		}
		else if(frame instanceof OpusFrame) {
			return CodecType.OPUS;
		}
		else if(frame instanceof SpeexFrame) {
			return CodecType.SPEEX;
		}
		else if(frame instanceof TheoraFrame) {
			return CodecType.THEORA;
		}
		else if(frame instanceof VorbisFrame) {
			return CodecType.VORBIS;
		}
		else if(frame instanceof Vp6Frame) {
			return CodecType.VP6;
		}
		else if(frame instanceof Vp8Frame) {
			return CodecType.VP8;
		}
		else if(frame instanceof Vp9Frame) {
			return CodecType.VP9;
		}
		return null;
	}
}
