package com.ttProject.frame.nellymoser.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.nellymoser.NellymoserFrame;
import com.ttProject.frame.nellymoser.type.Frame;
import com.ttProject.util.HexUtil;

/**
 * 無音frameテスト
 * @author taktod
 */
public class MutedFrameTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MutedFrameTest.class);
	@Test
	public void test() throws Exception {
		logger.info("無音frame動作テスト");
		Frame frame = NellymoserFrame.getMutedFrame(44100, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = NellymoserFrame.getMutedFrame(22050, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = NellymoserFrame.getMutedFrame(11025, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = NellymoserFrame.getMutedFrame(8000, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = NellymoserFrame.getMutedFrame(16000, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
	}
}
