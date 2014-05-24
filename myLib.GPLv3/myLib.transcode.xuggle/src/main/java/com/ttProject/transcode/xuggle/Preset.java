/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.transcode.xuggle;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IPixelFormat.Type;
import com.xuggle.xuggler.IStreamCoder.Direction;
import com.xuggle.xuggler.IStreamCoder.Flags;

/**
 * IStreamCoderのpresetデータ集
 * @author taktod
 */
public class Preset {
	/**
	 * mp3 44100Hz 48kbps 2channel
	 * @return
	 */
	public static IStreamCoder mp3() {
		IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_MP3);
		encoder.setSampleRate(44100);
		encoder.setChannels(2);
		encoder.setBitRate(48000);
		return encoder;
	}
	/**
	 * aac 44100Hz 48kbps 2channel
	 * @return
	 */
	public static IStreamCoder aac() {
		IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_AAC);
		encoder.setSampleRate(44100);
		encoder.setChannels(2);
		encoder.setBitRate(48000);
		return encoder;
	}
	/**
	 * vorbis 44100Hz 48kbps 2channel
	 * @return
	 */
	public static IStreamCoder vorbis() {
		IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_VORBIS);
		encoder.setSampleRate(44100);
		encoder.setChannels(2);
		encoder.setBitRate(48000);
		return encoder;
	}
	/**
	 * flv1 15fps 30gop 500kbps 320x240
	 * @return
	 */
	public static IStreamCoder flv1() {
		IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_FLV1);
		IRational frameRate = IRational.make(15, 1);
		encoder.setNumPicturesInGroupOfPictures(30);
		encoder.setBitRate(500000);
		encoder.setBitRateTolerance(9000);
		encoder.setWidth(320);
		encoder.setHeight(240);
		encoder.setGlobalQuality(10);
		encoder.setFrameRate(frameRate);
		encoder.setTimeBase(IRational.make(1, 1000));
		encoder.setPixelType(Type.YUV420P);
		return encoder;
	}
	/**
	 * h264 15fps 30gop 500kbps 320x240
	 * 細かい部分は適当(とりあえずうまくいったやつがはいっています。)
	 * @return
	 */
	public static IStreamCoder h264() {
		IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_H264);
		IRational frameRate = IRational.make(15, 1); // 15fps

		encoder.setBitRate(500000); // 500kbps
		encoder.setBitRateTolerance(9000);
		encoder.setWidth(320);
		encoder.setHeight(240);
		encoder.setGlobalQuality(10);
		encoder.setFrameRate(frameRate);
		encoder.setNumPicturesInGroupOfPictures(30);
		encoder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき) mpegtsなら90000か？
		encoder.setProperty("level", "30");
		encoder.setProperty("coder", "0");
		encoder.setProperty("qmin", "10");
		encoder.setProperty("bf", "0");
		encoder.setProperty("wprefp", "0");
		encoder.setProperty("cmp", "+chroma");
		encoder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
		encoder.setProperty("me_method", "hex");
		encoder.setProperty("subq", "5");
		encoder.setProperty("me_range", "16");
		encoder.setProperty("keyint_min", "25");
		encoder.setProperty("sc_threshold", "40");
		encoder.setProperty("i_qfactor", "0.71");
		encoder.setProperty("b_strategy", "0");
		encoder.setProperty("qcomp", "0.6");
		encoder.setProperty("qmax", "30");
		encoder.setProperty("qdiff", "4");
		encoder.setProperty("directpred", "0");
		encoder.setProperty("profile", "main");
		encoder.setProperty("cqp", "0");
		encoder.setFlag(Flags.FLAG_LOOP_FILTER, true);
		encoder.setFlag(Flags.FLAG_CLOSED_GOP, true);
		encoder.setPixelType(Type.YUV420P);
		return encoder;
	}
	/**
	 * vp8 15fps 30gop 500kbps 320x240
	 * @return
	 */
	public static IStreamCoder vp8() {
		IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_VP8);
		IRational frameRate = IRational.make(15, 1);
		encoder.setNumPicturesInGroupOfPictures(30);
		encoder.setBitRate(500000); // 500kbps
		encoder.setBitRateTolerance(9000);
		encoder.setWidth(320);
		encoder.setHeight(240);
		encoder.setGlobalQuality(10);
		encoder.setFrameRate(frameRate);
		encoder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		encoder.setPixelType(Type.YUV420P);
		return encoder;
	}
}
