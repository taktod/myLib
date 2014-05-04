package com.ttProject.frame.aac.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.type.Frame;
import com.ttProject.util.HexUtil;

/**
 * 無音frameについての動作テスト
 * @author taktod
 */
public class MutedFrameTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MutedFrameTest.class);
	@Test
	public void test() throws Exception {
		Frame frame = AacFrame.getMutedFrame(44100, 2, 16);
		logger.info(HexUtil.toHex(frame.getDecoderSpecificInfo().getData()));
		frame = AacFrame.getMutedFrame(22050, 2, 16);
		logger.info(HexUtil.toHex(frame.getDecoderSpecificInfo().getData()));
		frame = AacFrame.getMutedFrame(11025, 2, 16);
		logger.info(HexUtil.toHex(frame.getDecoderSpecificInfo().getData()));
		frame = AacFrame.getMutedFrame(44100, 1, 16);
		logger.info(HexUtil.toHex(frame.getDecoderSpecificInfo().getData()));
		frame = AacFrame.getMutedFrame(22050, 1, 16);
		logger.info(HexUtil.toHex(frame.getDecoderSpecificInfo().getData()));
		frame = AacFrame.getMutedFrame(11025, 1, 16);
		logger.info(HexUtil.toHex(frame.getDecoderSpecificInfo().getData()));
	}
}
