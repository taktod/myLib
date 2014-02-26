package com.ttProject.container.mkv.test;

import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mkv.type.CodecName;
import com.ttProject.container.mkv.type.DateUTC;
import com.ttProject.container.mkv.type.DocType;
import com.ttProject.container.mkv.type.Duration;
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
//	@Test
	public void signedIntTag() throws Exception {
		ReferenceBlock referenceBlock = new ReferenceBlock();
		referenceBlock.setValue(5);
		logger.info(HexUtil.toHex(referenceBlock.getData()));
	}
//	@Test
	public void stringTag() throws Exception {
		DocType docType = new DocType();
		docType.setValue("matroska");
		logger.info(HexUtil.toHex(docType.getData()));
	}
//	@Test
	public void utf8Tag() throws Exception {
		CodecName codecName = new CodecName();
		codecName.setValue("あいうえお");
		logger.info(HexUtil.toHex(codecName.getData()));
	}
//	@Test
	public void floatTag() throws Exception {
		Duration duration = new Duration();
		duration.setValue(120f);
		logger.info(HexUtil.toHex(duration.getData()));
		duration.setValue(120D);
		logger.info(HexUtil.toHex(duration.getData()));
	}
	@Test
	public void dateTag() throws Exception {
		DateUTC dateUtc = new DateUTC();
		dateUtc.setValue(new Date());
		logger.info(HexUtil.toHex(dateUtc.getData()));
	}
}
