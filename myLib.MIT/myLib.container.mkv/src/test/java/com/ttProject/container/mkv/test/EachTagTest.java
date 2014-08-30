/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.type.DateUTC;
import com.ttProject.container.mkv.type.DocType;
import com.ttProject.container.mkv.type.DocTypeReadVersion;
import com.ttProject.container.mkv.type.DocTypeVersion;
import com.ttProject.container.mkv.type.EBML;
import com.ttProject.container.mkv.type.MuxingApp;
import com.ttProject.container.mkv.type.ReferenceBlock;
import com.ttProject.container.mkv.type.SamplingFrequency;
import com.ttProject.container.mkv.type.SeekID;
import com.ttProject.container.mkv.type.Segment;
import com.ttProject.util.HexUtil;

/**
 * それぞれのtagについて、byteStream化したときにどうなるかのテスト
 * @author hps
 *
 */
public class EachTagTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(EachTagTest.class);
	/**
	 * 無限に続くデータをつくるための動作テストs
	 * @throws Exception
	 */
	@Test
	public void InfiniteEbmlTagTest() throws Exception {
		logger.info("InfiniteEbmlTagTest");
		Segment segment = new Segment();
		segment.setInfinite(true);
		logger.info(HexUtil.toHex(segment.getData(), true));
	}
	/**
	 * MkvBinaryTagの動作テスト
	 * @throws Exception
	 */
	@Test
	public void MkvBinaryTest() throws Exception {
		logger.info("MkvBinaryTest");
		SeekID seekId = new SeekID();
		seekId.setValue(HexUtil.makeBuffer("1549A966"));
		logger.info(HexUtil.toHex(seekId.getData(), true));
		// 53 AB 84 14 39 A9 66 
	}
	/**
	 * MkvDateTagの動作テスト
	 * @throws Exception
	 */
	@Test
	public void MkvDateTest() throws Exception {
		logger.info("MkvDateTest");
		DateUTC dateUTC = new DateUTC();
		Date d = new SimpleDateFormat("d M yyyy HH:mm:ss z", Locale.JAPAN).parse("02 06 2008 16:20:31 JST");
		dateUTC.setValue(d);
		logger.info(dateUTC.toString());
		logger.info(HexUtil.toHex(dateUTC.getData(), true));
		// 44 61 88 03 AF FA 97 5A 4A B6 00 
	}
	/**
	 * MkvFloatTagの動作テスト
	 * @throws Exception
	 */
	@Test
	public void MkvFloatTest() throws Exception {
		logger.info("MkvFloatTest");
		SamplingFrequency samplingFrequency = new SamplingFrequency();
		samplingFrequency.setValue(44100.0f);
		logger.info(HexUtil.toHex(samplingFrequency.getData(), true));
		// B5 84 47 2C 44 00
	}
	/**
	 * MkvMasterTagの動作テスト
	 * @throws Exception
	 */
	@Test
	public void MkvMasterTest() throws Exception {
		logger.info("MkvMasterTest");
		EBML ebml = new EBML();
		logger.info(ebml);
		DocType docType = new DocType();
		docType.setValue("matroska");
		ebml.addChild(docType);
		DocTypeVersion docTypeVersion = new DocTypeVersion();
		docTypeVersion.setValue(1);
		ebml.addChild(docTypeVersion);
		DocTypeReadVersion docTypeReadVersion = new DocTypeReadVersion();
		docTypeReadVersion.setValue(1);
		ebml.addChild(docTypeReadVersion);
		ebml.getData();
		logger.info(HexUtil.toHex(ebml.getData(), true));
		for(MkvTag tag : ebml.getChildList()) {
			logger.info(HexUtil.toHex(tag.getData(), true));
		}
		// 1A 45 DF A3 93 42 82 88 6D 61 74 72 6F 73 6B 61 42 87 81 01 42 85 81 01
	}
	/**
	 * MkvSignedIntTagの動作テスト
	 * 負の数の動作がおかしい
	 * @throws Exception
	 */
	@Test
	public void MkvSignedIntTest() throws Exception {
		logger.info("MkvSignedIntTest");
		ReferenceBlock referenceBlock = new ReferenceBlock();
		referenceBlock.setValue(5);
//		logger.info(referenceBlock);
		logger.info(HexUtil.toHex(referenceBlock.getData(), true));
		// FB 81 05(このデータは実データベースではないです。)
	}
	/**
	 * MkvStringTagの動作テスト
	 * @throws Exception
	 */
	@Test
	public void MkvStringTest() throws Exception {
		logger.info("MkvStringTest");
		DocType docType = new DocType();
		docType.setValue("matroska");
		logger.info(HexUtil.toHex(docType.getData(), true));
		// 42 82 88 6D 61 74 72 6F 73 6B 61
	}
	/**
	 * MkvUnsignedIntTagの動作テスト
	 * @throws Exception
	 */
	@Test
	public void MkvUnsignedIntTest() throws Exception {
		logger.info("MkvUnsignedIntTest");
		DocTypeVersion docTypeVersion = new DocTypeVersion();
		docTypeVersion.setValue(1);
		logger.info(HexUtil.toHex(docTypeVersion.getData(), true));
		// 42 87 81 01
	}
	/**
	 * MkvUtf8Tagの動作テスト
	 * @throws Exception
	 */
	@Test
	public void MkvUtf8Test() throws Exception {
		logger.info("MkvUtf8Test");
		MuxingApp muxingApp = new MuxingApp();
		muxingApp.setValue("libebml v0.7.7 + libmatroska v0.8.1");
		logger.info(HexUtil.toHex(muxingApp.getData(), true));
		// 4D 80 A3 6C 69 62 65 62 6D 6C 20 76 30 2E 37 2E 37 20 2B 20 6C 69 62 6D 61 74 72 6F 73 6B 61 20 76 30 2E 38 2E 31
	}
}