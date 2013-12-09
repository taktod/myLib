package com.ttProject.myLib.setup;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IContainer;

/**
 * myLib.frame用のメディアデータセットアップ
 * @author taktod
 */
public class Frame extends SetupBase {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(Frame.class);
	/**
	 * flv1の検証用データ
	 * @throws Exception
	 */
	@Test
	public void flv1() throws Exception {
		logger.info("flv1準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.frame.flv1", "test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.flv1(container), null);
	}
	@Test
	public void mp3() throws Exception {
		logger.info("mp3準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.frame.mp3", "test.mp3"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.mp3(container));
	}
}
