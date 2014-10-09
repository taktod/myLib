/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp8.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit4;

/**
 * bot loader for little endian test.
 * @author taktod
 */
public class LittleEndianBitloadTest {
	private Logger logger = Logger.getLogger(LittleEndianBitloadTest.class);
	@Test
	public void test() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(0x12345678);
		buffer.flip();
		buffer.order(ByteOrder.LITTLE_ENDIAN);
//		logger.info(Integer.toHexString(buffer.getInt()));
		IReadChannel channel = new ByteReadChannel(buffer);
		BitLoader loader = new BitLoader(channel);
		Bit4 b4a = new Bit4();
		Bit4 b4b = new Bit4();
		loader.load(b4a, b4b);
		logger.info(b4a);
		logger.info(b4b);
		// orderを変えても結果は同じ
	}
}
