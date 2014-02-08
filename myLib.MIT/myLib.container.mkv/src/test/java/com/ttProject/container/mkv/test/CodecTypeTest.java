package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mkv.CodecType;

/**
 * matroskaのコーデック情報判定動作用のテストコード
 * @author taktod
 */
public class CodecTypeTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(CodecTypeTest.class);
	/**
	 * テスト
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		// h264
		String codecString = "V_MPEG4/ISO/AVC";
		logger.info(CodecType.getCodecType(codecString));
		// mp3
		codecString = "A_MPEG/L3";
		logger.info(CodecType.getCodecType(codecString));
		// vp8
		codecString = "V_VP8";
		logger.info(CodecType.getCodecType(codecString));
		// vorbis
		codecString = "A_VORBIS";
		logger.info(CodecType.getCodecType(codecString));
		// AAc
		codecString = "A_AAC";
		logger.info(CodecType.getCodecType(codecString));
	}
}
