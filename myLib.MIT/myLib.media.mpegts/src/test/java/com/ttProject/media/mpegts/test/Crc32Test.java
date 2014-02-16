package com.ttProject.media.mpegts.test;

import java.nio.ByteBuffer;

import org.junit.Assert;

import com.ttProject.media.mpegts.Crc32;
import com.ttProject.util.HexUtil;

/**
 * crc32の動作確認
 * @author taktod
 */
public class Crc32Test {
	/**
	 * 計算があうか確認
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		Crc32 crc32 = new Crc32();
		// Patのサンプル474000100000B00D0001C100000001F0002AB104B2
		// 000B00D0001C100000001F000の部分が計算して2AB104B2になればよい
		ByteBuffer buffer = HexUtil.makeBuffer("00B00D0001C100000001F000");
		while(buffer.remaining() != 0) {
			crc32.update(buffer.get());
		}
		Assert.assertEquals("crc32の値確認", 0x2AB104B2, crc32.getValue());
	}
}
