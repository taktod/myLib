package com.ttProject.frame.adpcmimawav.test.rangecoder;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.util.HexUtil;

/**
 * rangeCoderの動作テスト
 * @author taktod
 */
public class RangeCoderTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(RangeCoderTest.class);
	/**
	 * エンコード動作
	 * @throws Exception
	 */
	@Test
	public void encodeTest() throws Exception {
		logger.info("Encode処理開始");
		RangeCoder coder = new RangeCoder();
/*		Integer[][] table = {
			{0x0,4},
			{0x1,4},
			{0x2,4},
			{0x3,3},
			{0x4,3},
			{0x5,3},
			{0x6,3},
			{0x7,2},
			{0x8,2},
			{0x9,2},
			{0xA,2},
			{0xB,2},
			{0xC,1},
			{0xD,1},
			{0xE,1},
			{0xF,1}
		};*/
		Integer[][] table = {
				{0xA, 4},
				{0xB, 2},
				{0xC, 1},
				{0xD, 1}
		};
		coder.setupTable(table);
		ByteBuffer data = HexUtil.makeBuffer("dcbbaaaa");
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
	@Test
	public void decodeTest() throws Exception {
		logger.info("Decode処理開始");
		ByteBuffer buffer = HexUtil.makeBuffer("FA800000");
		RangeCoder coder = new RangeCoder();
		Integer[][] table = {
				{0xA, 4},
				{0xB, 2},
				{0xC, 1},
				{0xD, 1}
		};
		coder.setupTable(table);
		coder.setDecodeTarget(buffer);
		int i = 0;
		while((i = coder.decodeData()) != -1) {
			logger.info(Integer.toHexString(i));
			Thread.sleep(100);
		}
	}
	/**
	 * 桁上がりテスト
	 * @throws Exception
	 */
	@Test
	public void carryUpTest() throws Exception {
		
	}
}
