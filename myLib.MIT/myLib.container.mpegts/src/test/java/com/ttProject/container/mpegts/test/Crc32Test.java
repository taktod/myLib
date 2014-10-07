/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mpegts.Crc32;
import com.ttProject.util.HexUtil;

/**
 * crc32 check test for mpegts
 * @author taktod
 */
public class Crc32Test {
	/** logger */
	private Logger logger = Logger.getLogger(Crc32Test.class);
	@Test
	public void test() throws Exception {
		Crc32 crc32 = new Crc32();
//		ByteBuffer buffer = HexUtil.makeBuffer("00B00D0001C100000001F000");
		ByteBuffer buffer = HexUtil.makeBuffer("00B00D5504C500000001E042");
		while(buffer.remaining() != 0) {
			crc32.update(buffer.get());
		}
		logger.info(Long.toHexString(crc32.getValue()));
	}
}
