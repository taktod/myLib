package com.ttProject.frame.mp3.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.frame.mp3.type.Frame;
import com.ttProject.util.HexUtil;

/**
 * 無音frameの動作テスト
 * @author taktod
 */
public class MutedFrameTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MutedFrameTest.class);
	@Test
	public void test() throws Exception {
		logger.info("無音frame動作テスト");
		Frame frame = Mp3Frame.getMutedFrame(44100, 2, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = Mp3Frame.getMutedFrame(22050, 2, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = Mp3Frame.getMutedFrame(11025, 2, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = Mp3Frame.getMutedFrame(44100, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = Mp3Frame.getMutedFrame(22050, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = Mp3Frame.getMutedFrame(11025, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
	}
}
