package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IContainer;

/**
 * やりとりすべきpacketデータについて調べるためのテスト
 * @author taktod
 */
public class PacketCheckTest extends SetupBase {
	/** ロガー */
	private Logger logger = Logger.getLogger(PacketCheckTest.class);
	/**
	 * 解析動作テストをします。
	 * @throws Exception
	 */
	@Test
	public void analyze() throws Exception {
		logger.info("解析テストします。");
		init();
		IContainer container = IContainer.make();
		if(container.open("target.webm", IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした。");
		}
		processConvert(container, Encoder.vp8(container), null);
	}
}
