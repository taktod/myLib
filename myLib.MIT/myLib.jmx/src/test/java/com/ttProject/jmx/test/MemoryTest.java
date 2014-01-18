package com.ttProject.jmx.test;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * メモリーの動作についてしらべてみる
 * @author taktod
 */
public class MemoryTest {
	private ByteBuffer buffer = null;
	/** ロガー */
	private Logger logger = Logger.getLogger(MemoryTest.class);
	@Test
	public void test() throws Exception {
//		System.gc();
		buffer = ByteBuffer.allocate(256);
		buffer.putInt(1);
		buffer.putInt(2);
		buffer.putInt(3);
		buffer.putInt(4);
		buffer.putInt(5);
		buffer.putInt(6);
		buffer.flip();
//		System.gc();
		List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
		for(MemoryPoolMXBean pool : pools) {
			logger.info(pool.getName());
			MemoryUsage usage = pool.getCollectionUsage();
			logger.info(usage);
		}
	}
	@Test
	public void test2() throws Exception {
		buffer = null;
		System.gc();
		List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
		for(MemoryPoolMXBean pool : pools) {
			logger.info(pool.getName());
			MemoryUsage usage = pool.getCollectionUsage();
			logger.info(usage);
		}
	}
}
