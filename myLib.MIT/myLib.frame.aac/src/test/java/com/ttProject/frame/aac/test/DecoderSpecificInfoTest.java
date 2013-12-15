package com.ttProject.frame.aac.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * dsiの読み込みテスト
 * @author taktod
 */
public class DecoderSpecificInfoTest {
	private Logger logger = Logger.getLogger(DecoderSpecificInfoTest.class);
	@Test
	public void restore() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("1210"));
		DecoderSpecificInfo specificInfo = new DecoderSpecificInfo();
		specificInfo.minimumLoad(channel);
		//decoderSpecificInfo: ot:00010 fi:0100 cc:0010 flf:0 docc0 ef:0
		logger.info(specificInfo);
	}
}
