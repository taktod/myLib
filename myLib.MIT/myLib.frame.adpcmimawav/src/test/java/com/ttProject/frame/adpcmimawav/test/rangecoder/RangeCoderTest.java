/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmimawav.test.rangecoder;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.util.HexUtil;

/**
 * rangeCoderの動作テスト
 * @author taktod
 */
public class RangeCoderTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(RangeCoderTest.class);
	private RangeCoder coder = new RangeCoder();
	{
		Integer[][] table = {
			{0x0,4}, // [ 0  4)
			{0x1,4}, // [ 4  8)
			{0x2,4}, // [ 8 12)
			{0x3,3}, // [12 15)
			{0x4,3}, // [15 18)
			{0x5,3}, // [18 21)
			{0x6,3}, // [21 24)
			{0x7,2}, // [24 26)
			{0x8,2}, // [26 28)
			{0x9,2}, // [28 30)
			{0xA,2}, // [30 32)
			{0xB,2}, // [32 34)
			{0xC,1}, // [34 35)
			{0xD,1}, // [35 36)
			{0xE,1}, // [36 37)
			{0xF,1}  // [37 38)
		};
/*		Integer[][] table = {
				{0xA, 4}, // [0 4)
				{0xB, 2}, // [4 6)
				{0xC, 1}, // [6 7)
				{0xD, 1}  // [7 8)
		};*/
		coder.setupTable(table);
	}
	/**
	 * エンコード動作
	 * @throws Exception
	 */
//	@Test
	public void encodeTest() throws Exception {
		logger.info("Encode処理開始");
		ByteBuffer data = HexUtil.makeBuffer("12345678");
		while(data.remaining() > 0) {
			byte b = data.get();
			coder.encodeData((b & 0xF0) >> 4);
			coder.encodeData(b & 0x0F);
		}
		logger.info(HexUtil.toHex(coder.getEncodeResult()));
	}
	/**
	 * デコード動作
	 * @throws Exception
	 */
//	@Test
	public void decodeTest() throws Exception {
		logger.info("Decode処理開始");
//		ByteBuffer buffer = HexUtil.makeBuffer("219D25598AA1");
		ByteBuffer buffer = HexUtil.makeBuffer("219D25598AA1");
		coder.setDecodeTarget(buffer);
		int i = 0;
		while((i = coder.decodeData()) != -1) {
			logger.info(Integer.toHexString(i));
//			Thread.sleep(100);
		}
	}
	/**
	 * 桁上がりテスト
	 * @throws Exception
	 */
//	@Test
	public void carryUpTest() throws Exception {
		
	}
}
