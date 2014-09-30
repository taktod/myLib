/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.myLib.setup;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

/**
 * setup for container.
 * @author taktod
 */
public class SetupForContainerTest extends SetupBase {
	/** logger */
	private Logger logger = Logger.getLogger(SetupForContainerTest.class);
	@Test
	public void adts() throws Exception {
		logger.info("adts setup (aac)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.adts", "test.aac.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.aac(container));
	}
	@Test
	public void flv() throws Exception {
		logger.info("flv setup (flv1)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.flv1(container), null);
		logger.info("flv setup (flv1 / mp3)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1mp3.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.flv1(container), Encoder.mp3(container));
		// mp3(8kHz) is valid, however, xuggle cannot deal with this.
/*		logger.info("flv setup (flv1 / mp38)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1mp38.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		IStreamCoder coder = Encoder.mp3(container);
		coder.setSampleRate(8000);
		processConvert(container, Encoder.flv1(container), coder);*/
		logger.info("flv setup (flv1 / adpcmswf)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1adpcmswf.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.flv1(container), Encoder.adpcm_swf(container));
		logger.info("flv setup (flv1 / nelly8)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1nelly8.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		IStreamCoder coder = Encoder.nellymoser(container);
		coder.setSampleRate(8000);
		coder.setChannels(1);
		processConvert(container, Encoder.flv1(container), coder);
		logger.info("flv setup (flv1 / nelly16)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1nelly16.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		coder = Encoder.nellymoser(container);
		coder.setSampleRate(16000);
		coder.setChannels(1);
		processConvert(container, Encoder.flv1(container), coder);
		logger.info("flv setup (flv1 / nelly)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1nelly.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		coder = Encoder.nellymoser(container);
		coder.setChannels(1);
		processConvert(container, Encoder.flv1(container), coder);
		logger.info("flv setup (h264 / mp3)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.h264mp3.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), Encoder.mp3(container));
		logger.info("flv setup (h264 / aac)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.h264aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("flv setup (h264 / speex)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.h264speex.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		coder = Encoder.speex(container);
		coder.setSampleRate(16000);
		coder.setChannels(1);
		processConvert(container, Encoder.h264(container), coder);
		// alaw and mulaw is valid, however, xuggle cannot deal with this.
/*		logger.info("flv setup (flv1 / pcm_alaw)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1pcm_alaw.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.flv1(container), Encoder.pcm_alaw(container));
		logger.info("flv setup (flv1 / pcm_mulaw)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1pcm_mulaw.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.flv1(container), Encoder.pcm_mulaw(container));*/
		
		logger.info("flv setup (adpcm44_2)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm44_2.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(44100);
		coder.setChannels(2);
		processConvert(container, null, coder);
		logger.info("flv setup (adpcm44_1)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm44_1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(44100);
		coder.setChannels(1);
		processConvert(container, null, coder);
		logger.info("flv setup (adpcm22_2)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm22_2.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(22050);
		coder.setChannels(2);
		processConvert(container, null, coder);
		logger.info("flv setup (adpcm22_1)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm22_1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(22050);
		coder.setChannels(1);
		processConvert(container, null, coder);
		logger.info("flv setup (adpcm11_2)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm11_2.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(11025);
		coder.setChannels(2);
		processConvert(container, null, coder);
		logger.info("flv setup (adpcm11_1)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm11_1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(11025);
		coder.setChannels(1);
		processConvert(container, null, coder);
	}
	@Test
	public void mkv() throws Exception {
		logger.info("mkv setup (h264 / mp3)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mkv", "test.h264mp3.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), Encoder.mp3(container));
		logger.info("mkv setup (h264 / aac)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mkv", "test.h264aac.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("mkv setup (mjpeg/adpcm_ima_wav)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mkv", "test.mjpegadpcmimawav.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container。");
		}
		processConvert(container, Encoder.mjpeg(container), Encoder.adpcm_ima_wav(container));
		logger.info("mkv setup (theora/vorbis)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mkv", "test.theoravorbis.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container。");
		}
		processConvert(container, Encoder.theora(container), Encoder.vorbis(container));
		logger.info("mkv setup (theora/speex)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mkv", "test.theoraspeex.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container。");
		}
		processConvert(container, Encoder.theora(container), Encoder.speex(container));
	}
	@Test
	public void mp3() throws Exception {
		logger.info("mp3 setup (mp3)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mp3", "test.mp3.mp3"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.mp3(container));
	}
	@Test
	public void mp4() throws Exception {
		logger.info("mp4 setup (h264 / aac)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mp4", "test.h264aac.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("mp4 setup (h264 / mp3)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mp4", "test.h264mp3.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), Encoder.mp3(container));
	}
	@Test
	public void mpegts() throws Exception {
		logger.info("mpegts setup (h264 / aac)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mpegts", "test.h264aac.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("mpegts setup (h264 / mp3)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mpegts", "test.h264mp3.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
	}
	@Test
	public void ogg() throws Exception {
		logger.info("ogg setup (vorbis)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.vorbis.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.vorbis(container));
		logger.info("ogg setup (speex)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.speex.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.speex(container));
		logger.info("ogg setup (speex)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.speex8.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		IStreamCoder encoder = Encoder.speex(container);
		encoder.setSampleRate(8000);
		processConvert(container, null, encoder);
		logger.info("ogg setup (speex)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.speex16.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		encoder = Encoder.speex(container);
		encoder.setSampleRate(16000);
		processConvert(container, null, encoder);
		logger.info("ogg setup (speex)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.speex32.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		encoder = Encoder.speex(container);
		encoder.setSampleRate(32000);
		processConvert(container, null, encoder);
		logger.info("ogg setup (theora / vorbis)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.theoravorbis.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.theora(container), Encoder.vorbis(container));
	}
	@Test
	public void webm() throws Exception {
		logger.info("webm setup (vp8 / vorbis)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.webm", "test.webm"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.vp8(container), Encoder.vorbis(container));
	}
	@Test
	public void wav() throws Exception {
		logger.info("wav setup (adpcm_ima_wav)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.wav", "test.adpcm_ima_wav.wav"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.adpcm_ima_wav(container));
		logger.info("wav setup (pcm_alaw)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.wav", "test.pcm_alaw.wav"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.pcm_alaw(container));
		logger.info("wav setup (pcm_mulaw)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.wav", "test.pcm_mulaw.wav"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.pcm_mulaw(container));
	}
	@Test
	public void test() throws Exception {
		logger.info("test aac(adts) setup");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "aac.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("test aac(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("test mp3(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "mp3.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.mp3(container));
		logger.info("test speex(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "speex.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		IStreamCoder encoder = Encoder.speex(container);
		encoder.setSampleRate(16000);
		encoder.setChannels(1);
		processConvert(container, null, encoder);
		logger.info("test h264(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "h264.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), null);
		logger.info("test h264/aac(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "h264_aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("test flv1(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "flv1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.flv1(container), null);
	}
}
