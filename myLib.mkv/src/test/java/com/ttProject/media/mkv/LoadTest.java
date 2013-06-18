package com.ttProject.media.mkv;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtils;

public class LoadTest {
	@Test
	public void test() throws Exception {
		IFileReadChannel channel = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("testffmpeg.webm")
		);
		ByteBuffer buffer = BufferUtil.safeRead(channel, 4);
		System.out.println(HexUtils.toHex(buffer, true));
	}
}
