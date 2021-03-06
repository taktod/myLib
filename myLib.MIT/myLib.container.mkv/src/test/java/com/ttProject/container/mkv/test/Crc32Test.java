/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.unit.extra.Crc32;
import com.ttProject.util.HexUtil;

/**
 * matroska has crc32, like ogg and mpegts. however, I don't know how...
 * @author taktod
 */
public class Crc32Test {
	/** logger */
	private Logger logger = Logger.getLogger(Crc32Test.class);
	@Test
	public void test() throws Exception {
		logger.info("check the result of crc32");
		// 67277B3A
//		ByteBuffer buffer = HexUtil.makeBuffer("1A45DFA399BF8467277B3A4282886D6174726F736B614287810242858102");
//		ByteBuffer buffer = HexUtil.makeBuffer("1A45DFA399BF84000000004282886D6174726F736B614287810242858102");
//		ByteBuffer buffer = HexUtil.makeBuffer("4282886D6174726F736B614287810242858102");
		ByteBuffer buffer = HexUtil.makeBuffer("BF84000000004282886D6174726F736B614287810242858102");
		Crc32 crc32 = new Crc32();
		do {
			crc32.update(buffer.get());
		}while(buffer.remaining() != 0);
		logger.info(Long.toHexString(crc32.getValue()));
	}
	public static class C32 extends Crc32 {
		/**
		 * initialize(must be 0xFFFFFFFF?)
		 */
		public void reset() {
			crc = 0xFFFFFFFFL;
		}
	}
}
