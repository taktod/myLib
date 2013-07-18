package com.ttProject.media.aac.test;

import org.junit.Test;

import com.ttProject.media.aac.DecoderSpecificInfo;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

public class DecoderSpecificInfoTest {
	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(new byte[]{
				0x12, 0x10
		});
		DecoderSpecificInfo specificInfo = new DecoderSpecificInfo(channel);
		System.out.println(specificInfo.dump());
	}
}
