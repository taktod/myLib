/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * test for byteBuffer.
 * @author taktod
 *
 */
public class BufferTest {
	/** loger */
	private Logger logger = Logger.getLogger(BufferTest.class);
	/**
	 * test
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		ByteBuffer buffer1, buffer2;
		buffer1 = ByteBuffer.allocate(0);
		buffer2 = ByteBuffer.allocate(0);
		buffer1.flip();
		buffer2.flip();
		logger.info(buffer1 == buffer2);
		logger.info(buffer1.equals(buffer2));
		buffer1 = ByteBuffer.allocate(4);
		buffer2 = ByteBuffer.allocate(4);
		buffer1.putInt(1);
		buffer2.putInt(2);
		buffer1.flip();
		buffer2.flip();
		logger.info(buffer1 == buffer2);
		logger.info(buffer1.equals(buffer2));
		buffer1 = ByteBuffer.allocate(4);
		buffer2 = ByteBuffer.allocate(4);
		buffer1.putInt(2);
		buffer2.putInt(2);
		buffer1.flip();
		buffer2.flip();
		logger.info(buffer1 == buffer2);
		logger.info(buffer1.equals(buffer2));
		buffer1 = ByteBuffer.allocate(4);
		buffer2 = ByteBuffer.allocate(4);
		buffer1.put((byte)0x00);
		buffer1.put((byte)0x00);
		buffer1.put((byte)0x00);
		buffer1.put((byte)0x02);
		buffer2.putInt(2);
		buffer1.flip();
		buffer2.flip();
		logger.info(buffer1 == buffer2);
		logger.info(buffer1.equals(buffer2));
	}
}
