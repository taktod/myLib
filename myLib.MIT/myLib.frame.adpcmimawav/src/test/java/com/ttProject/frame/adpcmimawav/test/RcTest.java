/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmimawav.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 最近学習したRangeCoderを使えばもっと圧縮効くのでは？
 * @author taktod
 */
public class RcTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(RcTest.class);
//	@Test
	public void test() throws Exception {
		logger.info("テスト開始");
		IReadChannel channel = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().
					getResource("test_mono.wav")
//					getResource("rtype_mono.wav")
		);
		channel.position(0x5C);
		long length = 0;
		while(channel.position() < channel.size()) {
			ByteBuffer buffer = BufferUtil.safeRead(channel, 0x400);
			buffer.position(4);
			while(buffer.remaining() > 0) {
				byte data = buffer.get();
				length ++;
			}
		}
		logger.info("オリジナルサイズ:" + Long.toHexString(length));
	}
	/*
	 * range coderの処理を実施します。
	 * 出現頻度は以下から適当にきめます。
13:09:21,369 [main] INFO [ChcTest] - 最終1位回数メモ
13:09:21,369 [main] INFO [ChcTest] - 0:16352 1:4217 2:4747 3:2926 4:0 5:0 6:0 7:0 8:11209 9:4878 10:5688 11:2906 12:0 13:0 14:0 15:0 
0 8 10 9 2 1 3 11 4 5 6 7 12 13 14 15

13:25:41,307 [main] INFO [ChcTest] - 最終15位回数メモ
13:25:41,307 [main] INFO [ChcTest] - 0:0 1:2 2:2079 3:253 4:7 5:23 6:10873 7:16504 8:0 9:0 10:0 11:0 12:0 13:7 14:9391 15:13784 
0 8 9 10 11 12 1 4 13 5 3 2 14 6 15 7

18:39:57,141 [main] INFO [ChcTest] - 最終1位回数メモ
18:39:57,142 [main] INFO [ChcTest] - 0:47170 1:21390 2:7042 3:5067 4:0 5:0 6:0 7:0 8:42730 9:23854 10:7446 11:4980 12:1 13:0 14:0 15:0 
0 8 9 1 11 10 2 3 12 4 5 6 7 13 14 15

18:38:38,436 [main] INFO [ChcTest] - 最終15位回数メモ
18:38:38,436 [main] INFO [ChcTest] - 0:0 1:67 2:55 3:35 4:36 5:84 6:33375 7:50789 8:0 9:0 10:0 11:0 12:0 13:2 14:29492 15:45745 
0 8 9 10 11 12 13 3 4 2 1 5 14 6 15 7

とりあえずテーブルは
[0 8 9] [1 2 10 11] [3 4 5 12 13] [6 7 14 15]
←でやすい →でにくい
出現頻度的にいくとこうなるか
	 * こっちもhuffmanと同じように、デフォルトのテーブルを決めたあとに、それぞれのframeについて1位と15位になるデータを入れ替えることにします。
	 * とりあえずデフォルトは
	 * 0: 4
	 * 8: 4
	 * 9: 4
	 * 1: 3
	 * 2: 3
	 * 10: 3
	 * 11: 3
	 * 3: 2
	 * 4: 2
	 * 5: 2
	 * 12: 2
	 * 13: 2
	 * 6: 1
	 * 7: 1
	 * 14: 1
	 * 15: 1
	 * としておいて計算します。
	 */
}
