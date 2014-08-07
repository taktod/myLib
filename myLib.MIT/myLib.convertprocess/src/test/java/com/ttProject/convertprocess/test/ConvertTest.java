/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvTagReader;
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
			ProcessManager manager = new ProcessManager();
			// ベースとなるプロセス
//			ProcessHandler handler = manager.getProcessHandler("test");
//			handler.setCommand("/usr/local/bin/avconv -y -copyts -i - -acodec copy -vcodec copy -f matroska ffout1.mkv");
//			handler.setTargetClass("com.ttProject.convertprocess.process.FlvOutputEntry");
			Map<String, String> env = new HashMap<String, String>();
			env.put("LD_LIBRARY_PATH", "/usr/local/lib");

			// 映像のみ
			final ProcessHandler videoHandler = manager.getProcessHandler("videoOnly");
			videoHandler.setCommand("/usr/local/bin/avconv -y -copyts -i - -an -vcodec mjpeg -s 160x120 -g 10 -q 20 -f matroska -");
			videoHandler.setTargetClass("com.ttProject.convertprocess.process.FlvVideoOutputEntry");
			videoHandler.setEnvExtra(env);

			// 音声のみ
			final ProcessHandler audioHandler = manager.getProcessHandler("audioOnly");
			audioHandler.setCommand("/usr/local/bin/avconv -y -copyts -i - -acodec adpcm_ima_wav -ar 22050 -ac 1 -vn -f matroska -");
			audioHandler.setTargetClass("com.ttProject.convertprocess.process.FlvAudioOutputEntry");
			audioHandler.setEnvExtra(env);

			manager.start();
			ExecutorService executor = Executors.newFixedThreadPool(2);
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						IReadChannel channel = videoHandler.getReadChannel();
						IReader reader = new MkvTagReader();
						IContainer container = null;
						while((container = reader.read(channel)) != null) {
							if(container instanceof MkvBlockTag) {
								MkvBlockTag blockTag = (MkvBlockTag)container;
								logger.info(blockTag.getFrame());
							}
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			});// */
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						IReadChannel channel = audioHandler.getReadChannel();
						IReader reader = new MkvTagReader();
						IContainer container = null;
						while((container = reader.read(channel)) != null) {
							if(container instanceof MkvBlockTag) {
								MkvBlockTag blockTag = (MkvBlockTag)container;
								logger.info(blockTag.getFrame().getPts());
							}
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			});// */
			// 開始後にデータを取り出すthreadをつくっておく。
			while((container = reader.read(source)) != null) {
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
