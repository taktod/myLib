package com.ttProject.unit.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.Ebml;

/**
 * mkvやwebmのebmlのデータ読み込みテスト
 * @author taktod
 */
public class EbmlTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(EbmlTest.class);
	/**
	 * 読み込みテスト
	 * @throws Exception
	 */
	@Test
	public void test1() throws Exception {
		logger.info("test1");
		// 1A45DFA3
		// 4286
		IReadChannel channel = new ByteReadChannel(new byte[]{
				0x1A, 0x45, (byte)0xDF, (byte)0xA3, 0x42, (byte)0x86
		});
		BitLoader loader = new BitLoader(channel);
		Ebml ebml1 = new Ebml();
		Ebml ebml2 = new Ebml();
		loader.load(ebml1, ebml2);
		logger.info(Long.toHexString(ebml1.getLong()));
		logger.info(Long.toHexString(ebml2.getLong()));
	}
}
