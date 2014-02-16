package com.ttProject.media.mpegts.test;

import org.apache.log4j.Logger;

import com.ttProject.media.mpegts.packet.Pat;
import com.ttProject.util.HexUtil;

/**
 * patの動作確認用テスト
 * @author taktod
 */
public class PatTest {
	private Logger logger = Logger.getLogger(PatTest.class);
	public void check() throws Exception {
		Pat pat = new Pat(HexUtil.makeBuffer("474000100000B00D0001C100000001F0002AB104B2"));
		logger.info(pat);
	}
//	@Test
	public void test() throws Exception {
		Pat pat = new Pat();
		logger.info(pat);
		logger.info(HexUtil.toHex(pat.getBuffer(), true));
	}
}
