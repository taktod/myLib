package com.ttProject.media.mpegts.test;

import org.apache.log4j.Logger;

import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.media.mpegts.packet.Pmt;
import com.ttProject.util.HexUtil;

/**
 * pmtの動作確認用テスト
 * @author taktod
 */
public class PmtTest {
	private Logger logger = Logger.getLogger(PmtTest.class);
	public void check() throws Exception {
		Pmt pmt = new Pmt(HexUtil.makeBuffer("475000100002B0170001C10000E100F0001BE100F0000FE101F0002F44B99B"));
		logger.info(pmt);
	}
//	@Test
	public void test() throws Exception {
		Pmt pmt = new Pmt();
		logger.info(pmt);
		logger.info(HexUtil.toHex(pmt.getBuffer(), true));
		pmt.addNewField(PmtElementaryField.makeNewField(CodecType.VIDEO_H264));
		logger.info(pmt);
		logger.info(HexUtil.toHex(pmt.getBuffer(), true));
		pmt.addNewField(PmtElementaryField.makeNewField(CodecType.AUDIO_AAC));
		logger.info(pmt);
		logger.info(HexUtil.toHex(pmt.getBuffer(), true));
	}
	public void fieldTest() throws Exception {
		PmtElementaryField field = PmtElementaryField.makeNewField(CodecType.VIDEO_H264);
		logger.info(field);
		field = PmtElementaryField.makeNewField(CodecType.AUDIO_AAC);
		logger.info(field);
	}
}
