/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.frameutil.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.util.HexUtil;
import com.ttProject.xuggle.frameutil.Depacketizer;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * 無音frameを作る動作テスト
 * @author taktod
 */
public class SilentFrameTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(SilentFrameTest.class);
	/**
	 * テスト動作
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		logger.info("test start");
		IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_VORBIS);
		encoder.setSampleRate(32000);
		encoder.setBitRate(48000);
		encoder.setChannels(1);
		if(encoder.open(null, null) < 0) {
			throw new Exception("failed to open encoder.");
		}
		ByteBuffer buffer = encoder.getExtraData().getByteBuffer(0, encoder.getExtraDataSize());
		logger.info(HexUtil.toHex(buffer, true));
		ICodec codec = encoder.getCodec();
		Depacketizer depacketizer = new Depacketizer();
		IAudioSamples.Format findFormat = null;
		for(IAudioSamples.Format format : codec.getSupportedAudioSampleFormats()) {
			if(findFormat == null) {
				findFormat = format;
			}
			if(format == IAudioSamples.Format.FMT_S16) {
				findFormat = format;
				break;
			}
		}
		if(findFormat == null) {
			throw new Exception("supported format is unknown.");
		}
		encoder.setSampleFormat(findFormat);
		if(encoder.open(null, null) < 0) {
			throw new Exception("failed to open audioEncoder.");
		}
		logger.info(encoder.getSampleRate());
		logger.info(encoder.getChannels());
		IAudioSamples samples = IAudioSamples.make(44100, encoder.getChannels(), findFormat);
		samples.setComplete(true, 44100, encoder.getSampleRate(), encoder.getChannels(), findFormat, 0);
		samples.setTimeBase(IRational.make(1, encoder.getSampleRate()));
		int sampleConsumed = 0;
		int lastCount = 0;
		IPacket packet = IPacket.make();
		while(sampleConsumed < samples.getNumSamples()) {
			int retval = encoder.encodeAudio(packet, samples, sampleConsumed);
			if(retval < 0) {
				throw new Exception("failed to encode.");
			}
			sampleConsumed += retval;
			if(packet.isComplete()) {
				IFrame frame = depacketizer.getFrame(encoder, packet);
				logger.info(frame.getCodecType());
				logger.info(HexUtil.toHex(frame.getData()));
				logger.info(frame.getData().remaining());
				IAudioFrame aFrame = (IAudioFrame) frame;
				logger.info(aFrame.getSampleNum() + " : " + (sampleConsumed - lastCount));
				lastCount = sampleConsumed;
			}
		}
		logger.info("end");
	}
}
