/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mpegts.MpegtsPacketReader;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mpegtsの動作テスト
 * @author taktod
 */
public class MpegtsTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsTest.class);
	/**
	 * 通常のフレームの動作テスト
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("test");
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.h264aac.ts")
			)
		);
	}
	/**
	 * iphone5Sで録画したnalの非常におおきなデータ
	 * 動作はするけど、遅すぎます
	 * @throws Exception
	 */
	@Test
	public void largeSliceTest() throws Exception {
		logger.info("largeSliceTest");
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/ahiru.ts"
			)
		);
	}
	private void analyzerTest(IFileReadChannel source) {
		try {
			IReader reader = new MpegtsPacketReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof Pes) {
					Pes pes = (Pes) container;
					// 再終端まできていないとデータがきません。
					IFrame frame = pes.getFrame();
					if(frame != null && frame instanceof IVideoFrame) {
						logger.info("pesFrame");
						if(frame instanceof VideoMultiFrame) {
							for(IVideoFrame vFrame : ((VideoMultiFrame) frame).getFrameList()) {
								logger.info(vFrame + ":" + vFrame.getSize() + "bytes" + " " + (vFrame.getPts() + 0) + " pts" + " " + vFrame.getDts() + " dts");
							}
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.warn("例外発生", e);
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {}
				source = null;
			}
		}
	}
}
