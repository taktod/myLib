/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.test;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.FlvTagWriter;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.xuggle.frameutil.Depacketizer;
import com.ttProject.xuggle.frameutil.Packetizer;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec.ID;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * try to decode
 * @author taktod
 */
public class DecodeTest {
	/** logger */
	private Logger logger = Logger.getLogger(DecodeTest.class);
	/** encode information */
	private int channels = 1;
	private int bitRate = 96000;
	private int sampleRate = 22050;
	private ICodec.ID codecId;

	/** encoder */
	private IStreamCoder encoder = null;
	/** shared packet for encode. */
	private IPacket packet = IPacket.make();

	/** decoder */
	private IStreamCoder decoder = null;
	/** shared packet for decode. */
	private IPacket decodedPacket = IPacket.make();

	/** frame -> packet */
	private Packetizer packetizer = new Packetizer();
	/** packet -> frame */
	private Depacketizer depacketizer = new Depacketizer();
	/** resampler */
	private IAudioResampler resampler = null;
	private FlvTagWriter writer = null;
	/**
	 * work test
	 */
//	@Test
	public void test() {
		try {
			IReadChannel source = FileReadChannel.openFileReadChannel(
					"xuggle_sound.error.flv" // timestampがおかしくてぷつぷついってうまく動作しなかった音声変換動作・・・原因はptsがおかしくなることだった
//					"xuggle_sound.error.flv" // timestampがおかしくてぷつぷついってうまく動作しなかった音声変換動作・・・原因はptsがおかしくなることだった
			);
			FlvTagReader reader = new FlvTagReader();
			writer = new FlvTagWriter("output4.flv");
			writer.prepareHeader(CodecType.AAC);
			codecId = ID.CODEC_ID_MP3; // try mp3
			openEncoder();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof VideoTag) {
//					logger.info(container);
				}
				else if(container instanceof AudioTag) {
//					logger.info(container);
					AudioTag aTag = (AudioTag) container;
					IAudioFrame aFrame = aTag.getFrame();
					if(aFrame instanceof AudioMultiFrame) {
						AudioMultiFrame multiFrame = (AudioMultiFrame)aFrame;
						for(IAudioFrame af : multiFrame.getFrameList()) {
							decodeSound(af);
						}
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * decode audioFrame
	 * @param audioFrame
	 */
	private void decodeSound(IAudioFrame aFrame) throws Exception {
		if(aFrame instanceof AudioMultiFrame) {
			AudioMultiFrame multiFrame = (AudioMultiFrame)aFrame;
			for(IAudioFrame af : multiFrame.getFrameList()) {
				decodeSound(af);
			}
			return;
		}
		// need to check how many samples after decode.
		decoder = packetizer.getDecoder(aFrame, decoder);
		if(decoder == null) {
			logger.warn("decoder is null");
			return;
		}
		if(!decoder.isOpen()) {
			logger.info("open decoder");
			if(decoder.open(null, null) < 0) {
				throw new Exception("failed to open decoder");
			}
		}
		IPacket pkt = packetizer.getPacket(aFrame, decodedPacket);
		if(pkt == null) {
			return;
		}
		decodedPacket = pkt;
		IAudioSamples samples = IAudioSamples.make(aFrame.getSampleNum(), decoder.getChannels());
		int offset = 0;
		while(offset < decodedPacket.getSize()) {
			int bytesDecoded = decoder.decodeAudio(samples, decodedPacket, offset);
			if(bytesDecoded < 0) {
				throw new Exception("failed decode packet");
			}
			offset += bytesDecoded;
			if(samples.isComplete()) {
				// sometime need resample.
				samples = getResampled(samples);
				encodeSound(samples);
			}
		}
	}
	/**
	 * do resample to fit encoder.
	 * @param samples
	 * @return
	 */
	private IAudioSamples getResampled(IAudioSamples samples) throws Exception {
		if(samples.getSampleRate() != encoder.getSampleRate()
		|| samples.getFormat()     != encoder.getSampleFormat()
		|| samples.getChannels()   != encoder.getChannels()) {
			if(resampler == null
			||    (samples.getSampleRate() != resampler.getInputRate()
				|| samples.getFormat()     != resampler.getInputFormat()
				|| samples.getChannels()   != resampler.getInputChannels())) {
				// in the case of no resampler, or format is not fit, make new resampler.(if we do this many times, miss the sample counter, a little.)
				// (audio will delay from video a little.)
				logger.info("open resampler");
				logger.info(samples.getSampleRate());
				logger.info(samples.getFormat());
				logger.info(samples.getChannels());
				resampler = IAudioResampler.make(
						encoder.getChannels(), samples.getChannels(),
						encoder.getSampleRate(), samples.getSampleRate(),
						encoder.getSampleFormat(), samples.getFormat());
			}
			IAudioSamples spl = IAudioSamples.make(1024, encoder.getChannels());
			int retval = resampler.resample(spl, samples, samples.getNumSamples());
			if(retval <= 0) {
				throw new Exception("failed to resample audio.");
			}
			spl.setPts(samples.getPts());
			spl.setTimeBase(samples.getTimeBase());
			return spl;
		}
		else {
			return samples;
		}
	}
	/**
	 * try encode
	 */
	private void encodeSound(IAudioSamples samples) throws Exception {
		int sampleConsumed = 0;
		while(sampleConsumed < samples.getNumSamples()) {
			int retval = encoder.encodeAudio(packet, samples, sampleConsumed);
			if(retval < 0) {
				throw new Exception("failed to encode.");
			}
			sampleConsumed += retval;
			if(packet.isComplete()) {
				IFrame frame = depacketizer.getFrame(encoder, packet);
				logger.info("made frame:" + frame);
				writer.addFrame(0x08, frame);
			}
		}
	}
	/**
	 * open the encoder
	 * @throws Exception
	 */
	private synchronized void openEncoder() throws Exception {
		if(encoder == null) {
			IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, codecId);
			coder.setChannels(channels);
			coder.setSampleRate(sampleRate);
			coder.setBitRate(bitRate);
			encoder = coder;
			ICodec codec = encoder.getCodec();
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
				throw new Exception("supported audioFormat is undefined.");
			}
			encoder.setSampleFormat(findFormat);
			if(encoder.open(null, null) < 0) {
				throw new Exception("failed to open encoder.s");
			}
		}
	}
}
