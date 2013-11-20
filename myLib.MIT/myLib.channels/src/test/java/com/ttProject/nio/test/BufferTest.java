package com.ttProject.nio.test;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.ttProject.nio.CacheBuffer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

public class BufferTest {
	private Logger logger = Logger.getLogger(BufferTest.class);
//	@Test
	public void test() throws Exception {
		IReadChannel target = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("tmp.dat")
		);
		CacheBuffer buffer = new CacheBuffer(target, target.size());
		while(buffer.remaining() != 0) {
			logger.info(buffer.getInt());
		}
	}
//	@Test
	public void test2() throws Exception {
		IReadChannel target = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("tmp.dat")
		);
		while(target.position() != target.size()) {
			ByteBuffer buffer = ByteBuffer.allocate(4);
			target.read(buffer);
			buffer.flip();
			logger.info(buffer.getInt());
		}
	}
//	@Test
	public void test3() throws Exception {
		IReadChannel target = new ByteReadChannel("test".getBytes());
		ByteBuffer buffer = ByteBuffer.allocate(10);
		target.read(buffer);
		buffer.flip();
		logger.info(buffer.remaining());
		while(buffer.remaining() != 0) {
			logger.info(Integer.toHexString(buffer.get() & 0xFF));
		}
		target.close();
	}
//	@Test
	public void test4() throws Exception {
		FileOutputStream fos = new FileOutputStream("test");
		FileChannel channel = fos.getChannel();
		logger.info(channel.isOpen());
		fos.close();
		logger.info(channel.isOpen());
	}
}
