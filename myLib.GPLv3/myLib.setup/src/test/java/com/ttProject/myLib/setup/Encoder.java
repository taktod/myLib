/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.myLib.setup;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Flags;

/**
 * setup helper for encoders.
 * @author taktod
 */
public class Encoder {
	/**
	 * mp3
	 * @param container
	 * @return
	 */
	public static IStreamCoder mp3(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_MP3);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * adpcm_swf
	 * @param container
	 * @return
	 */
	public static IStreamCoder adpcm_swf(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_ADPCM_SWF);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * adpcm_ima_wav
	 * @param container
	 * @return
	 */
	public static IStreamCoder adpcm_ima_wav(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_ADPCM_IMA_WAV);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
//		audioEncoder.setBitRate(96000); // nonsence for bitrate
		return audioEncoder;
	}
	/**
	 * nerrymoser
	 * @param container
	 * @return
	 */
	public static IStreamCoder nellymoser(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_NELLYMOSER);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * nerrymoser
	 * @param container
	 * @return
	 */
	public static IStreamCoder speex(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_SPEEX);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(32000);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * aac
	 * @param container
	 * @return
	 */
	public static IStreamCoder aac(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_AAC);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * vorbis
	 * @param container
	 * @return
	 */
	public static IStreamCoder vorbis(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_VORBIS);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * pcm_alaw
	 * @param container
	 * @return
	 */
	public static IStreamCoder pcm_alaw(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_PCM_ALAW);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(8000);
		audioEncoder.setChannels(1);
		audioEncoder.setBitRate(64000);
		return audioEncoder;
	}
	/**
	 * pcm_mulaw
	 * @param container
	 * @return
	 */
	public static IStreamCoder pcm_mulaw(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_PCM_MULAW);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(8000);
		audioEncoder.setChannels(1);
		audioEncoder.setBitRate(64000);
		return audioEncoder;
	}
	/**
	 * h264
	 * @param container
	 * @return
	 */
	public static IStreamCoder h264(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_H264);
		IStreamCoder videoEncoder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		videoEncoder.setNumPicturesInGroupOfPictures(5); // gop = 5
		
		videoEncoder.setBitRate(650000); // 650kbps
		videoEncoder.setBitRateTolerance(9000);
		videoEncoder.setWidth(320);
		videoEncoder.setHeight(240);
		videoEncoder.setGlobalQuality(10);
		videoEncoder.setFrameRate(frameRate);
		videoEncoder.setTimeBase(IRational.make(1, 1000)); // 1/1000
		videoEncoder.setProperty("level", "30");
		videoEncoder.setProperty("coder", "0");
		videoEncoder.setProperty("qmin", "10");
		videoEncoder.setProperty("bf", "0");
		videoEncoder.setProperty("wprefp", "0");
		videoEncoder.setProperty("cmp", "+chroma");
		videoEncoder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
		videoEncoder.setProperty("me_method", "hex");
		videoEncoder.setProperty("subq", "5");
		videoEncoder.setProperty("me_range", "16");
		videoEncoder.setProperty("keyint_min", "25");
		videoEncoder.setProperty("sc_threshold", "40");
		videoEncoder.setProperty("i_qfactor", "0.71");
		videoEncoder.setProperty("b_strategy", "0");
		videoEncoder.setProperty("qcomp", "0.6");
		videoEncoder.setProperty("qmax", "30");
		videoEncoder.setProperty("qdiff", "4");
		videoEncoder.setProperty("directpred", "0");
		videoEncoder.setProperty("profile", "main");
		videoEncoder.setProperty("cqp", "0");
		videoEncoder.setFlag(Flags.FLAG_LOOP_FILTER, true);
		videoEncoder.setFlag(Flags.FLAG_CLOSED_GOP, true);
		return videoEncoder;
	}
	/**
	 * vp8
	 * @param container
	 * @return
	 */
	public static IStreamCoder vp8(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_VP8);
		IStreamCoder videoEncoder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		videoEncoder.setNumPicturesInGroupOfPictures(5); // gop=5
		videoEncoder.setBitRate(650000); // 650kbps
		videoEncoder.setBitRateTolerance(9000);
		videoEncoder.setWidth(320);
		videoEncoder.setHeight(240);
		videoEncoder.setGlobalQuality(10);
		videoEncoder.setFrameRate(frameRate);
		videoEncoder.setTimeBase(IRational.make(1, 1000));
		return videoEncoder;
	}
	/**
	 * flv1
	 * @return
	 */
	public static IStreamCoder flv1(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_FLV1);
		IStreamCoder videoEncoder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		videoEncoder.setNumPicturesInGroupOfPictures(5); // gop=5
		videoEncoder.setBitRate(650000); // 650kbps
		videoEncoder.setBitRateTolerance(9000);
		videoEncoder.setWidth(320);
		videoEncoder.setHeight(240);
		videoEncoder.setGlobalQuality(10);
		videoEncoder.setFrameRate(frameRate);
		videoEncoder.setTimeBase(IRational.make(1, 1000));
		return videoEncoder;
	}
	/**
	 * theora
	 * @return
	 */
	public static IStreamCoder theora(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_THEORA);
		IStreamCoder videoEncoder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		videoEncoder.setNumPicturesInGroupOfPictures(5); // gop=5
		videoEncoder.setBitRate(650000); // 650kbps
		videoEncoder.setBitRateTolerance(9000);
		videoEncoder.setWidth(320);
		videoEncoder.setHeight(240);
		videoEncoder.setGlobalQuality(10);
		videoEncoder.setFrameRate(frameRate);
		videoEncoder.setTimeBase(IRational.make(1, 1000));
		return videoEncoder;
	}
	/**
	 * mjpeg
	 * @param container
	 * @return
	 */
	public static IStreamCoder mjpeg(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_MJPEG);
		IStreamCoder videoEncoder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1);
		videoEncoder.setNumPicturesInGroupOfPictures(5); // all frame is key frame, no meaning for gop?
		videoEncoder.setBitRate(650000);
		videoEncoder.setBitRateTolerance(9000);
		videoEncoder.setWidth(320);
		videoEncoder.setHeight(240);
		videoEncoder.setGlobalQuality(10);
		videoEncoder.setFrameRate(frameRate);
		videoEncoder.setTimeBase(IRational.make(1, 1000));
		return videoEncoder;
	}
}
