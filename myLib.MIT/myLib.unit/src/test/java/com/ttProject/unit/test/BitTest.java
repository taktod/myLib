package com.ttProject.unit.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit6;
import com.ttProject.unit.extra.BitLoader;

/**
 * bitの読み込み動作テスト
 * @author taktod
 */
public class BitTest {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(BitTest.class);
	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(new byte[]{
			0x12, 0x34, 0x56, 0x78	
		});
		BitLoader bitLoader = new BitLoader(channel);
		Bit1 bit1 = new Bit1();
		Bit2 bit2 = new Bit2();
		Bit6 bit5 = new Bit6();
		bitLoader.load(bit1, bit2, bit5);
		logger.info(bit1);
		logger.info(bit2);
		logger.info(bitLoader.getExtraBit());
		logger.info(channel.position());
		logger.info(channel.size());
	}
}
