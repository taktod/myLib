/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.opus.test;

import org.junit.Test;

import com.ttProject.frame.opus.OpusFrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * codecPrivateデータの読み込みテスト
 * @author taktod
 */
public class CodecPrivateTest {
	/**
	 * テスト
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("4F7075734865616401023400401F0000000000"));
		// 先頭8文字はOpusHeadになっているはず。
		OpusFrameAnalyzer analyzer = new OpusFrameAnalyzer();
		analyzer.analyze(channel);
	}
}
