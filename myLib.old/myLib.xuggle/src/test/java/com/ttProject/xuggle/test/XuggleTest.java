/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.test;

import org.apache.log4j.Logger;

import com.ttProject.util.HexUtil;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * xuggleの動作について調べておくテスト
 * @author taktod
 *
 */
public class XuggleTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(XuggleTest.class);
	/**
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		IContainer container = IContainer.make();
		if(container.open("speex.ogg", IContainer.Type.READ, null) < 0) {
			throw new Exception("ファイルがひらけませんでした。");
		}
		int numStreams = container.getNumStreams();
		for(int i = 0;i < numStreams;i ++) {
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			IBuffer buffer = coder.getExtraData();
			switch(coder.getCodecType()) {
			case CODEC_TYPE_AUDIO:
				logger.info(HexUtil.toHex(buffer.getByteArray(0, buffer.getSize()), true));
				break;
			case CODEC_TYPE_VIDEO:
				break;
			default:
				break;
			}
		}
	}
}
