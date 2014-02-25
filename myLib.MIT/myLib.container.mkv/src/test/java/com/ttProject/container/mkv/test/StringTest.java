package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.util.HexUtil;

/**
 * 文字列に関する動作チェック
 * @author taktod
 *
 */
public class StringTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(StringTest.class);
	@Test
	public void test() throws Exception {
		String a = "あいうえお";
		logger.info(a.length());
		logger.info(a.getBytes().length);
		logger.info(HexUtil.toHex(a.getBytes()));
		logger.info(HexUtil.toHex(a.getBytes("UTF-8")));
	}
}
