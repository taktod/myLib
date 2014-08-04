/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.convertprocess.ProcessHandler;
import com.ttProject.convertprocess.ProcessManager;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * コンバートを実施する動作テスト
 * @author taktod
 */
public class ConvertTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(ConvertTest.class);
	@Test
	public void test() {
		IReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.flv");
			// Flvなので、FlvReaderで読み取る
			IReader reader = new FlvTagReader();
			IContainer container = null;
			// 変換に必要なもの。
			ProcessManager manager = new ProcessManager();
			ProcessHandler handler = manager.getProcessHandler("test");
			handler.setCommand("/usr/local/bin/avconv -i - -acodec copy -vcodec copy -f flv -");
			handler.setTargetClass("com.ttProject.convertprocess.process.FlvOutputEntry");
			ProcessHandler audioHandler = manager.getProcessHandler("audioOnly");
			audioHandler.setCommand("");
			handler.setTargetClass("com.ttProject.convertprocess.process.FlvAudioOutputEntry");
			manager.start();
			while((container = reader.read(source)) != null) {
				logger.info(container);
				if(container instanceof VideoTag) {
					manager.pushFrame(((VideoTag) container).getFrame(), 0x09);
				}
				else if(container instanceof AudioTag) {
					manager.pushFrame(((AudioTag) container).getFrame(), 0x08);
				}
			}
			logger.info("10秒まっておく");
			Thread.sleep(10000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
		}
	}
}
