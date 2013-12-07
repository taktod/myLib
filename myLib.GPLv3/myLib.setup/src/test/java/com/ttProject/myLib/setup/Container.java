package com.ttProject.myLib.setup;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IContainer;

public class Container extends SetupBase {
	/** ロガー */
	private Logger logger = Logger.getLogger(Container.class);
	/**
	 * flvの検証用データ
	 * @throws Exception
	 */
	@Test
	public void flv() throws Exception {
		logger.info("flv準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.flv1(container), null);
	}
}
