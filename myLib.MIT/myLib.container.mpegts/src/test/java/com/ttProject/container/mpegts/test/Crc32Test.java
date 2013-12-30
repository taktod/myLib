package com.ttProject.container.mpegts.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mpegts.Crc32;
import com.ttProject.util.HexUtil;

/**
 * crc32について確認するテスト
 * @author taktod
 */
public class Crc32Test {
	/** ロガー */
	private Logger logger = Logger.getLogger(Crc32Test.class);
	@Test
	public void test() throws Exception {
		Crc32 crc32 = new Crc32();
		ByteBuffer buffer = HexUtil.makeBuffer("00B00D0001C100000001F000");
		while(buffer.remaining() != 0) {
			crc32.update(buffer.get());
		}
		logger.info(Long.toHexString(crc32.getValue()));
	}
}
