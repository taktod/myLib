/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.util.HexUtil;

public class BufferTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(BufferTest.class);
	/**
	 * テスト
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("test");
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put((byte)0x01);
		buffer.put((byte)0x02);
		buffer.put((byte)0x03);
		buffer.put((byte)0x04);
		buffer.flip();
		logger.info(HexUtil.toHex(buffer));
	}
	/**
	 * テスト２
	 * @throws Exception
	 */
	@Test
	public void test2() throws Exception {
		logger.info("test2");
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(0x01020304);
		buffer.flip();
		logger.info(HexUtil.toHex(buffer));
	}
	/**
	 * テスト３
	 * @throws Exception
	 */
	@Test
	public void test3() throws Exception {
		logger.info("test3");
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN); // speexのデータを書き込む時にはlittle endianにすべきみたいですね。
		buffer.putInt(0x01020304);
		buffer.flip();
		logger.info(HexUtil.toHex(buffer));
	}
	/**
	 * テスト４
	 * @throws Exception
	 */
	@Test
	public void test4() throws Exception {
		logger.info("test4");
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(1);
		buffer.flip();
		buffer.order(ByteOrder.BIG_ENDIAN);
		logger.info(buffer.getInt()); // bitEndianにすると、1677726が帰ってくる。
	}
	/**
	 * テスト５
	 * @throws Exception
	 */
	@Test
	public void test5() throws Exception {
		logger.info("test5");
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put((byte)0x01);
		buffer.put((byte)0x00);
		buffer.put((byte)0x00);
		buffer.put((byte)0x00);
		buffer.flip();
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		logger.info(buffer.getInt()); // little endianにすればきちんと取得できる。
	}
}
