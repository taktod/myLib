package com.ttProject.nio.test;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.ttProject.nio.CustomBuffer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

public class BufferTest {
	@Test
	public void test() throws Exception {
		IFileReadChannel target = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("tmp.dat")
		);
		CustomBuffer buffer = new CustomBuffer(target, target.size());
		while(buffer.remaining() != 0) {
			System.out.println(buffer.getInt());
		}
	}
	@Test
	public void test2() throws Exception {
		IFileReadChannel target = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("tmp.dat")
		);
		while(target.position() != target.size()) {
			ByteBuffer buffer = ByteBuffer.allocate(4);
			target.read(buffer);
			buffer.flip();
			System.out.println(buffer.getInt());
		}
	}
}
