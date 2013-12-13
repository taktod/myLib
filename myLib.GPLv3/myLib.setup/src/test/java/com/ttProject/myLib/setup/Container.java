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
	/**
	 * oggの検証用データ
	 * @throws Exception
	 */
	@Test
	public void ogg() throws Exception {
		logger.info("ogg準備 (vorbis)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.vorbis.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, null, Encoder.vorbis(container));
		logger.info("ogg準備 (speex)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.speex.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, null, Encoder.speex(container));
	}
}
