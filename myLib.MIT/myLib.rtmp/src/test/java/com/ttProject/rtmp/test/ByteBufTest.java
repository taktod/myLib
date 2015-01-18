/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.apache.log4j.Logger;

import com.ttProject.util.HexUtil;

public class ByteBufTest {
	private Logger logger = Logger.getLogger(ByteBufTest.class);
	public void test() throws Exception {
		ByteBuf buf = Unpooled.buffer(4);
		logger.info("readable?:" + buf.readableBytes());
		logger.info("writable?:" + buf.writableBytes());
//		buf.writeInt(0x80000702);
//		buf.setInt(0, 0x80000702);
		logger.info(buf.isWritable());
		buf.writeBytes(new byte[]{(byte)0x80, 0x00, 0x07, 0x02});
//		buf.writerIndex(4);
		logger.info(HexUtil.toHex(buf.array()));
		logger.info("readable?:" + buf.readableBytes());
		logger.info("writable?:" + buf.writableBytes());
		logger.info(buf.isWritable());
	}
}
