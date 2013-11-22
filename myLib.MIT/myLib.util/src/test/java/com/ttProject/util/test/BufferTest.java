package com.ttProject.util.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

/**
 * bufferに関する動作テスト
 * @author taktod
 *
 */
public class BufferTest {
	private Logger logger = Logger.getLogger(BufferTest.class);
	@Test
	public void test() {
		ByteBuffer src = HexUtil.makeBuffer("000102030405");
//		ByteBuffer dst = HexUtil.makeBuffer("000102030405");
		ByteBuffer dst = ByteBuffer.allocate(8);
		dst.put((byte)0);
		dst.put((byte)1);
		dst.put((byte)2);
		dst.put((byte)3);
		dst.put((byte)4);
		dst.put((byte)5);
		dst.flip();
		// なぜかhashCodeが一致する
		logger.info(src.hashCode());
		logger.info(dst.hashCode());
		logger.info(src);
		logger.info(dst);
		logger.info(BufferUtil.isSame(src, dst));
	}
}