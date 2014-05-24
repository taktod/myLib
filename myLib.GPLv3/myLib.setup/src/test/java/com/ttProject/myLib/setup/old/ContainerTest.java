/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.myLib.setup.old;

import org.apache.log4j.Logger;

import com.ttProject.myLib.setup.Encoder;
import com.ttProject.myLib.setup.SetupBase;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

/**
 * myLib.container.testで利用するデータ
 * @author taktod
 */
public class ContainerTest extends SetupBase {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(ContainerTest.class);
	/**
	 * aac用変換元データ
	 * @throws Exception
	 */
//	@Test
	public void aac() throws Exception {
		logger.info("aac(adts)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "aac.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
	}
	/**
	 * flv用変換元データ
	 * @throws Exception
	 */
//	@Test
	public void flv() throws Exception {
		logger.info("aac(flv)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("mp3(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "mp3.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.mp3(container));
		logger.info("speex(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "speex.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		IStreamCoder encoder = Encoder.speex(container);
		encoder.setSampleRate(16000);
		encoder.setChannels(1);
		processConvert(container, null, encoder);
		logger.info("h264(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "h264.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), null);
		logger.info("h264/aac(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "h264_aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("flv1(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "flv1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.flv1(container), null);
	}
}
