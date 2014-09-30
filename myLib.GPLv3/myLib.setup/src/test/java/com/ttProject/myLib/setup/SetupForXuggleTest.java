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
 * setup for xuggle projects.
 * @author taktod
 */
public class SetupForXuggleTest extends SetupBase {
	/** ロガー */
	private Logger logger = Logger.getLogger(SetupForXuggleTest.class);
	@Test
	public void adts() throws Exception {
		logger.info("aac(adts) setup");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.aac(container));
	}
	@Test
	public void flv() throws Exception {
		logger.info("aac(flv) setup");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("adpcmswf(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "adpcmswf.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.adpcm_swf(container));
		logger.info("flv1(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "flv1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.flv1(container), null);
		logger.info("h264(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "h264.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), null);
		logger.info("mp3(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "mp3.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.mp3(container));
		logger.info("nellymoser(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "nellymoser.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		IStreamCoder encoder = Encoder.nellymoser(container);
		encoder.setChannels(1);
		processConvert(container, null, encoder);
		logger.info("speex(flv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "speex.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		encoder = Encoder.speex(container);
		encoder.setSampleRate(16000);
		encoder.setChannels(1);
		processConvert(container, null, encoder);
	}
	@Test
	public void mkv() throws Exception {
		logger.info("aac(mkv) setup");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("h264(mkv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "h264.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), null);
		logger.info("mp3(mkv) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "mp3.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.mp3(container));
	}
	@Test
	public void mp3() throws Exception {
		logger.info("mp3(mp3) setup");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "mp3.mp3"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.mp3(container));
	}
	@Test
	public void mp4() throws Exception {
		logger.info("aac(mp4) setup");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("h264(mp4) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "h264.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), null);
		logger.info("mp3(mp4) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "mp3.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.mp3(container));
	}
	@Test
	public void mpegts() throws Exception {
		logger.info("aac(mpegts) setup");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("h264(mpegts) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "h264.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.h264(container), null);
	}
	@Test
	public void ogg() throws Exception {
		logger.info("speex(ogg) setup");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "speex.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.speex(container));
		logger.info("vorbis(ogg) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "vorbis.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.vorbis(container));
	}
	@Test
	public void webm() throws Exception {
		logger.info("vp8(webm) setup");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "vp8.webm"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, Encoder.vp8(container), null);
		logger.info("vorbis(webm) setup");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "vorbis.webm"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open container");
		}
		processConvert(container, null, Encoder.vorbis(container));
	}
}
