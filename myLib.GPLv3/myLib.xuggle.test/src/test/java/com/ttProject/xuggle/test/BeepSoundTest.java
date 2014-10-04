/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.IContainer;

/**
 * make beep sound.
 * @author taktod
 */
public class BeepSoundTest {
	/** logger */
	private Logger logger = Logger.getLogger(BeepSoundTest.class);
	/**
	 * play beep test.
	 * @throws Exception
	 */
//	@Test
	public void playTest() throws Exception {
		logger.info("start playTest");
		SourceDataLine audioLine = null;
		IAudioSamples samples = beepSamples();
		
		AudioFormat format = new AudioFormat((float)samples.getSampleRate(), (int)samples.getSampleBitDepth(), samples.getChannels(), true, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		audioLine = (SourceDataLine)AudioSystem.getLine(info);
		audioLine.open(format);
		logger.info("beepstart");
		audioLine.start();
		audioLine.write(samples.getData().getByteArray(0, samples.getSize()), 0, samples.getSize());
		audioLine.drain();
		logger.info("beepend");
		audioLine.close();
		audioLine = null;
	}
	/**
	 * make flv(nellymoser)test.
	 * @throws Exception
	 */
	@Test
	public void flvNellymoserTest() throws Exception {
		logger.info("flvNellymoserTest");
		IContainer container = IContainer.make();
		if(container.open("flv_nellymoser.flv", IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container.");
		}
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_NELLYMOSER);
		IStreamCoder encoder = stream.getStreamCoder();
		encoder.setSampleRate(44100);
		encoder.setChannels(1);
		encoder.setBitRate(96000);
		processConvert(container, encoder);
		logger.info("end");
	}
	/**
	 * process convertTask.
	 * @param container
	 * @param encoder
	 * @throws Exception
	 */
	public void processConvert(IContainer container, IStreamCoder encoder) throws Exception {
		ICodec codec = encoder.getCodec();
		Format findFormat = null;
		for(Format format : codec.getSupportedAudioSampleFormats()) {
			if(findFormat == null) {
				findFormat = format;
			}
			if(findFormat == Format.FMT_S16) {
				findFormat = format;
				break;
			}
		}
		if(findFormat == null) {
			throw new Exception("audioSampleFormat is unknwon.");
		}
		logger.info("found format:" + findFormat);
		encoder.setSampleFormat(findFormat);
		if(encoder.open(null, null) < 0) {
			throw new Exception("failed to open audioEncoder");
		}
		if(container.writeHeader() < 0) {
			throw new Exception("failed to write header.");
		}
		IPacket packet = IPacket.make();
		IAudioSamples samples = beepSamples();
		if(samples.getSampleRate() != encoder.getSampleRate()
		|| samples.getFormat() != encoder.getSampleFormat()
		|| samples.getChannels() != encoder.getChannels()) {
			IAudioResampler resampler = IAudioResampler.make(
					encoder.getChannels(), samples.getChannels(),
					encoder.getSampleRate(), samples.getSampleRate(),
					encoder.getSampleFormat(), samples.getFormat());
			IAudioSamples spl = IAudioSamples.make(samples.getNumSamples(), encoder.getChannels());
			int retVal = resampler.resample(spl, samples, samples.getNumSamples());
			if(retVal <= 0) {
				throw new Exception("failed to audio resample.");
			}
			samples = spl;
		}
		int samplesConsumed = 0;
		while(samplesConsumed < samples.getNumSamples()) {
			int retVal = encoder.encodeAudio(packet, samples, samplesConsumed);
			if(retVal < 0) {
				throw new Exception("failed to audio encode.");
			}
			samplesConsumed += retVal;
			if(packet.isComplete()) {
				if(container.writePacket(packet) < 0) {
					throw new Exception("failed to write container.");
				}
			}
		}
		if(container.writeTrailer() < 0) {
			throw new Exception("failed to write trailer.");
		}
	}
	/**
	 * make sine wave xuggler IAudioSamples.
	 * @return
	 */
	private IAudioSamples beepSamples() {
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
		
		IAudioSamples samples = IAudioSamples.make(sampleNum, channel, Format.FMT_S16);
		samples.getData().put(buffer.array(), 0, 0, buffer.remaining());
		samples.setComplete(true, sampleNum, sampleRate, channel, Format.FMT_S16, 0);
		samples.setTimeStamp(0);
		samples.setPts(0);
		return samples;
	}
}
