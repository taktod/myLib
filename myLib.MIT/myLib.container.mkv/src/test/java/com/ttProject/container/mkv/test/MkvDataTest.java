package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mkv.type.EBMLVersion;
import com.ttProject.container.mkv.type.ReferenceBlock;
import com.ttProject.util.HexUtil;

/**
 * mkv用のデータの構築動作テストをしたい。
 * @author taktod
 */
public class MkvDataTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvDataTest.class);
	/**
	 * 
	 * @throws Exception
	 */
//	@Test
	public void unsignedIntTag() throws Exception {
		EBMLVersion version = new EBMLVersion();
		version.setValue(1);
		logger.info(HexUtil.toHex(version.getData()));
	}
	@Test
	public void signedIntTag() throws Exception {
		ReferenceBlock referenceBlock = new ReferenceBlock();
		referenceBlock.setValue(5);
		logger.info(HexUtil.toHex(referenceBlock.getData()));
	}
}
