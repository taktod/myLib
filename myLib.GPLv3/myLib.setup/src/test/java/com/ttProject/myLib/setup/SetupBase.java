/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.myLib.setup;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * basic task for setup.
 * @author taktod
 */
public class SetupBase {
	/** counter for audioFrame */
	private int audioCounter = 0;
	/** counter for videoFrame */
	private int videoCounter = 0;
	/**
	 * initialize
	 */
	protected void init() {
		audioCounter = 0;
		videoCounter = 0;
	}
	/**
	 * process convert
	 * @param container
	 * @param videoEncoder
	 * @param audioEncoder
	 */
	protected void processConvert(IContainer container, IStreamCoder videoEncoder, IStreamCoder audioEncoder) throws Exception {
		IVideoResampler videoResampler = null;
		IAudioResampler audioResampler = null;
		try {
			if(videoEncoder != null) {
				ICodec codec = videoEncoder.getCodec();
				// need to check pixel type.
				// if YUV420P is accepted, use it.
				IPixelFormat.Type findType = null;
				for(IPixelFormat.Type type : codec.getSupportedVideoPixelFormats()) {
					if(findType == null) {
						findType = type;
					}
					if(type == IPixelFormat.Type.YUV420P) {
						findType = type;
						break;
					}
				}
				if(findType == null) {
					throw new Exception("pixel format is unknown, error.");
				}
				videoEncoder.setPixelType(findType);
				if(videoEncoder.open(null, null) < 0) {
					throw new Exception("cannot open videoEncoder.");
				}
			}
			if(audioEncoder != null) {
				ICodec codec = audioEncoder.getCodec();
				// need to check audioFormat.
				// if S16 is accepted, use it.
				IAudioSamples.Format findFormat = null;
				for(IAudioSamples.Format format : codec.getSupportedAudioSampleFormats()) {
					if(findFormat == null) {
						findFormat = format;
					}
					if(findFormat == IAudioSamples.Format.FMT_S16) {
						findFormat = format;
						break;
					}
				}
				if(findFormat == null) {
					throw new Exception("audioSampleFormat is unknown, error.");
				}
				audioEncoder.setSampleFormat(findFormat);
				if(audioEncoder.open(null, null) < 0) {
					throw new Exception("cannot open audioEncoder.");
				}
			}
			// open container header.
			if(container.writeHeader() < 0) {
				throw new Exception("failed to write header.");
			}
			IPacket packet = IPacket.make();
			// start packet writing.
			while(true) {
				IVideoPicture picture = null;
				if(videoEncoder != null) {
					picture = image();
					if(picture.getWidth() != videoEncoder.getWidth()
					|| picture.getHeight() != videoEncoder.getHeight()
					|| picture.getPixelType() != videoEncoder.getPixelType()) {
						if(videoResampler == null) {
							videoResampler = IVideoResampler.make(
									videoEncoder.getWidth(), videoEncoder.getHeight(), videoEncoder.getPixelType(),
									picture.getWidth(), picture.getHeight(), picture.getPixelType());
						}
						IVideoPicture pct = IVideoPicture.make(videoEncoder.getPixelType(), videoEncoder.getWidth(), videoEncoder.getHeight());
						int retVal = videoResampler.resample(pct, picture);
						if(retVal <= 0) {
							throw new Exception("failed to picture resample.");
						}
						picture = pct;
					}
					if(videoEncoder.encodeVideo(packet, picture, 0) < 0) {
						throw new Exception("failed to picture encode.");
					}
					if(packet.isComplete()) {
						if(container.writePacket(packet) < 0) {
							System.out.println(packet);
							packet.setDts(packet.getPts());
							throw new Exception("failed to write video track.");
						}
					}
				}
				if(audioEncoder != null) {
					while(true) {
						IAudioSamples samples = samples();
						if(samples.getSampleRate() != audioEncoder.getSampleRate() 
						|| samples.getFormat() != audioEncoder.getSampleFormat()
						|| samples.getChannels() != audioEncoder.getChannels()) {
							if(audioResampler == null) {
								audioResampler = IAudioResampler.make(
										audioEncoder.getChannels(), samples.getChannels(),
										audioEncoder.getSampleRate(), samples.getSampleRate(),
										audioEncoder.getSampleFormat(), samples.getFormat());
							}
							IAudioSamples spl = IAudioSamples.make(1024, audioEncoder.getChannels());
							int retVal = audioResampler.resample(spl, samples, samples.getNumSamples());
							if(retVal <= 0) {
								throw new Exception("failed to audio resample.");
							}
							samples = spl;
						}
						int samplesConsumed = 0;
						while(samplesConsumed < samples.getNumSamples()) {
							int retval = audioEncoder.encodeAudio(packet, samples, samplesConsumed);
							if(retval < 0) {
								throw new Exception("failed to audio encode.");
							}
							samplesConsumed +=  retval;
							if(packet.isComplete()) {
								packet.setDts(packet.getPts());
								if(container.writePacket(packet) < 0) {
									throw new Exception("failed to write audio track.");
								}
							}
						}
						if((picture != null && samples.getPts() > picture.getPts())
								|| samples.getPts() > 1000000) {
							break;
						}
					}
				}
				if(picture == null || picture.getPts() > 1000000) {
					break;
				}
			}
			if(container.writeTrailer() < 0) {
				throw new Exception("failed to write container tailer.");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(videoEncoder != null) {
				videoEncoder.close();
			}
			if(audioEncoder != null) {
				audioEncoder.close();
			}
			if(container != null) {
				container.close();
			}
		}
	}
	/**
	 * make audio beep sound. 440Hz
	 * @return
	 */
	private IAudioSamples samples() {
		int samplingRate = 44100;
		int tone = 440;
		int bit = 16;
		int channels = 2;
		int samplesNum = 1024;
		ByteBuffer buffer = ByteBuffer.allocate((int)samplesNum * bit * channels / 8);
		double rad = tone * 2 * Math.PI / samplingRate; // radian for each sample.
		double max = (1 << (bit - 2)) - 1; // max of ampletude
		// make buffer
		buffer.order(ByteOrder.LITTLE_ENDIAN); // xuggle need little endian for this.
		long startPos = 1000 * audioCounter / 44100 * 1000;
		for(int i = 0;i < samplesNum / 8;i ++, audioCounter ++) {
			short data = (short)(Math.sin(rad * audioCounter) * max);
			for(int j = 0;j < channels;j ++) {
				buffer.putShort(data);
			}
		}
		buffer.flip();
		int snum = (int)(buffer.remaining() * 8/bit/channels);
		// make audioSamples.
		IAudioSamples samples = IAudioSamples.make(snum, channels, Format.FMT_S16);
		samples.getData().put(buffer.array(), 0, 0, buffer.remaining());
		samples.setComplete(true, snum, samplingRate, channels, Format.FMT_S16, 0);
		// timestamp is needed.
		samples.setTimeStamp(startPos);
		// this seems not to be necessary, however, just do it.
		samples.setPts(startPos);
		return samples;
	}
	/**
	 * make picture data. with random number.
	 * @return
	 */
	private IVideoPicture image() {
		// for instance, make random image with 10 fps.
		BufferedImage base = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
		String message = Integer.toString((int)(Math.random() * 1000));
		Graphics g = base.getGraphics();
		g.setColor(Color.white);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
		g.drawString(message, 100, 100);
		g.dispose();
		// make xuggle picture.
		IConverter converter = ConverterFactory.createConverter(base, IPixelFormat.Type.YUV420P);
		IVideoPicture picture = converter.toPicture(base, 25000 * videoCounter);
		// setPts do setTimestamp inside.
		picture.setPts(25000 * videoCounter);
		videoCounter ++;
		return picture;
	}
	/**
	 * make target file.
	 * @param path
	 * @param file
	 * @return
	 */
	protected String getTargetFile(String project, String file) {
		String target = "../" + project + "/src/test/resources/" + file;
		String[] data = target.split("/");
		File f = new File(".");
		f = new File(f.getAbsolutePath());
		f = f.getParentFile().getParentFile();
		for(String path : data) {
			f = new File(f.getAbsolutePath(), path);
		}
		f.getParentFile().mkdirs();
		return f.getAbsolutePath();
	}
}
