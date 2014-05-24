/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mpegts.test;

import org.apache.log4j.Logger;

import com.ttProject.media.mpegts.descriptor.ServiceDescriptor;
import com.ttProject.media.mpegts.packet.Sdt;
import com.ttProject.util.HexUtil;

/**
 * sdtを生成するときの動作テスト
 * @author taktod
 */
public class SdtTest {
	private Logger logger = Logger.getLogger(SdtTest.class);
	/**
	 * sdtのデータ確認動作テスト
	 * @throws Exception
	 */
//	@Test
	public void check() throws Exception {
		Sdt sdt = new Sdt(HexUtil.makeBuffer("474011100042F0240001C100000001FF0001FC8013481101054C696261760953657276696365303168C5DB49"));
		logger.info(sdt);
	}
//	@Test
	public void test() throws Exception {
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("taktodTools", "mpegtsMuxer");
		logger.info(sdt);
		logger.info(HexUtil.toHex(sdt.getBuffer(), true));
	}
	/**
	 * descriptorの書き込みテスト
	 * @throws Exception
	 */
	public void descriptor() throws Exception {
		ServiceDescriptor descriptor = new ServiceDescriptor();
		descriptor.setName("taktod", "test");
		logger.info(descriptor);
	}
}
