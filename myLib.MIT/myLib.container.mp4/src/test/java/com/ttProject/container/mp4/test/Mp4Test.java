/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mp4.Mp4AtomReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mp4データの読み込み動作テスト
 * @author taktod
 */
public class Mp4Test {
	/** ロガー */
	private Logger logger = Logger.getLogger(Mp4Test.class);
	/**
	 * 解析動作テスト
	 */
	@Test
	public void analyzeTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.h264aac.mp4")
			);
			IReader reader = new Mp4AtomReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
			}
		}
		catch(Exception e) {
			logger.warn(e);
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
