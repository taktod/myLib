package com.ttProject.frame.h265.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.h265.ConfigData;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * h265のconfigDataの解析動作テスト
 * @author taktod
 */
public class ConfigTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(ConfigTest.class);
	/**
	 * 動作テスト
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("configの読み込み動作テスト");
		IReadChannel target = new ByteReadChannel(HexUtil.makeBuffer(
				"0101600000008000000000005DF000FCFDF8F800000F03200001001840010C01FFFF01600000030080000003000003005D95C090210001002942010101600000030080000003000003005DA0078200B459657924DAF010100000030030000005608022000100064401C173D189"));
		ConfigData configData = new ConfigData();
		configData.analyze(target);
		logger.info("configの読み込み動作テストおわり");
	}
}
