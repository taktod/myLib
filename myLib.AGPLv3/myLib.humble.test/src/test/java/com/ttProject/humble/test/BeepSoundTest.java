/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU AFFERO GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.humble.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import io.humble.ferry.Buffer;
import io.humble.video.AudioChannel.Layout;
import io.humble.video.AudioFormat.Type;
import io.humble.video.Codec;
import io.humble.video.Coder.Flag;
import io.humble.video.Encoder;
import io.humble.video.MediaAudio;
import io.humble.video.MediaAudioResampler;
import io.humble.video.MediaPacket;
import io.humble.video.Muxer;
import io.humble.video.Rational;

import org.apache.log4j.Logger;

/**
 * make beep sound.
 * @author taktod
 */
public class BeepSoundTest {
	/** logger */
	private Logger logger = Logger.getLogger(BeepSoundTest.class);
	/**
	 * play beep Test.
	 */
//	@Test
	public void playTest() throws Exception {
		logger.info("start playTest");
		SourceDataLine audioLine = null;
		MediaAudio samples = beepSamples();
		logger.info("sample is ready");
		
		AudioFormat format = new AudioFormat((float)samples.getSampleRate(), (int)samples.getBytesPerSample() * 8, samples.getChannels(), true, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		audioLine = (SourceDataLine)AudioSystem.getLine(info);
		audioLine.open(format);
		logger.info("beepstart");
		audioLine.start();
		Buffer buffer = samples.getData(0);
		audioLine.write(buffer.getByteArray(0, samples.getDataPlaneSize(0)), 0, samples.getDataPlaneSize(0));
		audioLine.drain();
		logger.info("beepend");
		audioLine.close();
		audioLine = null;
	}
//	@Test
	public void flvNellymoserTest() throws Exception {
		logger.info("flvNellymoserTest");
		Muxer muxer = Muxer.make("flv_nellymoser.flv", null, null);
		Encoder encoder = Encoder.make(Codec.findEncodingCodec(Codec.ID.CODEC_ID_NELLYMOSER));
		Type findType = null;
		for(Type type : encoder.getCodec().getSupportedAudioFormats()) {
			logger.info(type);
			if(findType == null) {
				findType = type;
			}
			if(type == Type.SAMPLE_FMT_S16) {
				findType = type;
				break;
			}
		}
		logger.info(findType);
		encoder.setSampleRate(44100);
		encoder.setChannels(1);
		encoder.setChannelLayout(Layout.CH_LAYOUT_MONO);
		encoder.setSampleFormat(findType);
		encoder.setFlag(Flag.FLAG_GLOBAL_HEADER, true);
		encoder.open(null, null);
		muxer.addNewStream(encoder);
		processConvert(muxer, encoder);
		logger.info("done");
	}
//	@Test
	public void oggVorbisTest() throws Exception {
		logger.info("oggVorbisTest");
		Muxer muxer = Muxer.make("ogg_vorbis.ogg", null, null);
		Encoder encoder = Encoder.make(Codec.findEncodingCodec(Codec.ID.CODEC_ID_VORBIS));
		Type findType = null;
		for(Type type : encoder.getCodec().getSupportedAudioFormats()) {
			logger.info(type);
			if(findType == null) {
				findType = type;
			}
			if(type == Type.SAMPLE_FMT_S16) {
				findType = type;
				break;
			}
		}
		logger.info(findType);
		encoder.setSampleRate(44100);
		encoder.setChannels(2);
		encoder.setChannelLayout(Layout.CH_LAYOUT_STEREO);
		encoder.setSampleFormat(findType);
		encoder.setFlag(Flag.FLAG_GLOBAL_HEADER, true);
		encoder.open(null, null);
		muxer.addNewStream(encoder);
		processConvert(muxer, encoder);
		logger.info("done");
	}
//	@Test
	public void flvMp3Test() throws Exception {
		logger.info("flvMp3Test");
		Muxer muxer = Muxer.make("flvMp3.flv", null, null);
		Encoder encoder = Encoder.make(Codec.findEncodingCodec(Codec.ID.CODEC_ID_MP3));
		Type findType = null;
		for(Type type : encoder.getCodec().getSupportedAudioFormats()) {
			logger.info(type);
			if(findType == null) {
				findType = type;
			}
			if(type == Type.SAMPLE_FMT_S16) {
				findType = type;
				break;
			}
		}
		logger.info(findType);
		encoder.setSampleRate(44100);
		encoder.setChannels(2);
		encoder.setChannelLayout(Layout.CH_LAYOUT_STEREO);
		encoder.setSampleFormat(findType);
		encoder.setFlag(Flag.FLAG_GLOBAL_HEADER, true);
		encoder.open(null, null);
		muxer.addNewStream(encoder);
		processConvert(muxer, encoder);
		logger.info("done");
	}
	private void processConvert(Muxer muxer, Encoder encoder) throws Exception {
		muxer.open(null, null);
		MediaPacket packet = MediaPacket.make();
		MediaAudio samples = beepSamples();
		logger.info(samples);
		if(samples.getSampleRate() != encoder.getSampleRate()
		|| samples.getFormat() != encoder.getSampleFormat()
		|| samples.getChannelLayout() != encoder.getChannelLayout()) {
			MediaAudioResampler resampler = MediaAudioResampler.make(
					encoder.getChannelLayout(), encoder.getSampleRate(), encoder.getSampleFormat(),
					samples.getChannelLayout(), samples.getSampleRate(), samples.getFormat());
			resampler.open();
			MediaAudio spl = MediaAudio.make(samples.getNumSamples(), encoder.getSampleRate(), encoder.getChannels(), encoder.getChannelLayout(), encoder.getSampleFormat());
			resampler.resample(spl, samples);
			logger.info(spl);
			logger.info(spl.getNumSamples());
			samples = spl;
		}
		logger.info(samples);
		while(true) {
			// need to resample.
			encoder.encodeAudio(packet, samples);
			if(!packet.isComplete()) {
				break;
			}
			logger.info(packet);
			muxer.write(packet, false);
		}
		muxer.close();
	}
	/**
	 * resample MediaAudio from non planed buffer to planed buffer.
	 * (ex: S16 -> S16P)
	 * @param input
	 * @return
	 */
	public MediaAudio resample(MediaAudio input) {
		return null;
	}
	/**
	 * make sine wave humble MediaAudio.
	 * @return
	 */
	private MediaAudio beepSamples() {
		int sampleRate = 44100; // 44.1KHz
		int sampleNum  = 44100; // 44100 samples(1sec)
		int channel    = 2;     // 2channel(stereo)
		int tone       = 440;   // 440Hz tone.
		int bit        = 16;    // 16bit
		ByteBuffer buffer = ByteBuffer.allocate((int)sampleNum * bit * channel / 8);
		double rad = tone * 2 * Math.PI / sampleRate; // radian for each sample.
		double max = (1 << (bit - 2)) - 1; // ampletude
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for(int i = 0;i < sampleNum;i ++) {
			short data = (short)(Math.sin(rad * i) * max);
			for(int j = 0;j < channel;j ++) {
				buffer.putShort(data);
			}
		}
		buffer.flip();
		
		logger.info("data size for 1sec buffer.:" + buffer.remaining());
		MediaAudio samples = MediaAudio.make(sampleNum, sampleRate, channel, Layout.CH_LAYOUT_STEREO, Type.SAMPLE_FMT_S16);
		samples.getData(0).put(buffer.array(), 0, 0, buffer.remaining());
		logger.info(samples.getDataPlaneSize(0)); // why this size is little bit bigger than original buffer?
		samples.setComplete(true);
		samples.setTimeBase(Rational.make(1, 44100));
		samples.setTimeStamp(0);
		samples.setNumSamples(sampleNum);
		return samples;
	}
}
