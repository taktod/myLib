package com.ttProject.container.ogg.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.ttProject.container.ogg.Crc32;
import com.ttProject.util.HexUtil;

public class Crc32Test {
	/** ロガー */
	private Logger logger = Logger.getLogger(Crc32Test.class);
//	@Test
	public void test() {
		logger.info("crc32動作テスト:mpegtsのコピー");
		Crc32 crc32 = new Crc32();
		// Patのサンプル474000100000B00D0001C100000001F0002AB104B2
		// 000B00D0001C100000001F000の部分が計算して2AB104B2になればよい
		ByteBuffer buffer = HexUtil.makeBuffer("00B00D0001C100000001F000");
		while(buffer.remaining() != 0) {
			crc32.update(buffer.get());
		}
		Assert.assertEquals("crc32の値確認", 0x2AB104B2, crc32.getValue());
	}
	@Test
	public void test2() {
		Crc32 crc32 = new Crc32();
		ByteBuffer buffer = HexUtil.makeBuffer("4F676753000200000000000000003B93BCA5000000000000000001505370656578202020312E3272633100000000000000000000000000000100000050000000007D0000020000000400000002000000E0AB0000800200000000000001000000000000000000000000000000");
		while(buffer.remaining() != 0) {
			crc32.update(buffer.get());
		}
		logger.info(Long.toHexString(crc32.getValue()));
		crc32 = new Crc32();
		buffer = HexUtil.makeBuffer("4F676753000000000000000000003B93BCA50100000000000000012C0C0000004C61766635342E322E3130300100000014000000656E636F6465723D4C61766635342E322E313030");
		while(buffer.remaining() != 0) {
			crc32.update(buffer.get());
		}
		logger.info(Long.toHexString(crc32.getValue()));
	}
}
