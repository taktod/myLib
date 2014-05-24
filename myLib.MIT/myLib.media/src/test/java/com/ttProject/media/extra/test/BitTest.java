/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.extra.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitConnector;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.extra.Seg;
import com.ttProject.media.extra.Ueg;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * bitやりとりの動作確認テスト
 * @author taktod
 */
public class BitTest {
	private Logger logger = Logger.getLogger(BitTest.class);
	/**
	 * 読み込み書き込みテスト
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(new byte[] {
				(byte)0xFF, (byte)0xF1, 0x50, (byte)0x80, 0x02, 0x1F, (byte)0xFC
		});
		Bit4 syncBit1 = new Bit4();
		Bit8 syncBit2 = new Bit8();
		Bit1 id = new Bit1();
		Bit2 layer = new Bit2();
		Bit1 protectionAbsent = new Bit1();
		Bit2 profile = new Bit2();
		Bit4 samplingFrequenceIndex = new Bit4();
		Bit1 privateBit = new Bit1();
		Bit3 channelConfiguration = new Bit3();
		Bit1 originalFlg = new Bit1();
		Bit1 home = new Bit1();
		Bit1 copyrightIdentificationBit = new Bit1();
		Bit1 copyrightIdentificationStart = new Bit1();
		Bit5 frameSize1 = new Bit5();
		Bit8 frameSize2 = new Bit8();
		Bit3 adtsBufferFullness1 = new Bit3();
		Bit8 adtsBufferFullness2 = new Bit8();
		Bit2 noRawDataBlocksInFrame = new Bit2();
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(
			syncBit1, syncBit2, id, layer, protectionAbsent, profile, samplingFrequenceIndex,
			privateBit, channelConfiguration, originalFlg, home,
			copyrightIdentificationBit, copyrightIdentificationStart, frameSize1, frameSize2,
			adtsBufferFullness1, adtsBufferFullness2, noRawDataBlocksInFrame);
		channel.close();
		
		BitConnector bitConnector = new BitConnector();
		ByteBuffer buffer = bitConnector.connect(
				syncBit1, syncBit2, id, layer, protectionAbsent, profile, samplingFrequenceIndex,
				privateBit, channelConfiguration, originalFlg, home,
				copyrightIdentificationBit, copyrightIdentificationStart, frameSize1, frameSize2,
				adtsBufferFullness1, adtsBufferFullness2, noRawDataBlocksInFrame);
		logger.info(HexUtil.toHex(buffer.array(), true));
	}
	/**
	 * extGolomb付きの動作テスト
	 * @throws Exception
	 */
//	@Test
	public void test2() throws Exception {
		IReadChannel channel = new ByteReadChannel(new byte[] {
				(byte)0xF7
		});
		Bit1 a = new Bit1();
		Bit2 b = new Bit2();
		Bit1 c = new Bit1();
		Ueg ueg = new Ueg();
		Seg seg = new Seg();
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(a, b, c, ueg, seg);
		logger.info(a);
		logger.info(b);
		logger.info(c);
		logger.info(ueg);
		logger.info(seg);
	}
}
