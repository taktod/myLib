package com.ttProject.flazr.test;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.message.MessageType;
import com.ttProject.flazr.rtmp.message.MetadataAmf3;

public class MetadataAmf3Test {
	/**
	 * Amf3の動作テストを実施します。
	 */
	@Test
	public void test() {
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(new byte[] {
			0x00, 0x02, 0x00, 0x0A, 0x6F, 0x6E, 0x4D, 0x65, 0x74, 0x61, 0x44, 0x61, 0x74, 0x61,
			0x11, 0x0A, 0x0B, 0x01, 0x0B, 0x77, 0x69, 0x64, 0x74, 0x68, 0x04, (byte)0x81, 0x20, 0x05, 0x69, 0x64, 0x04, 0x00, 0x0D, 0x68, 0x65, 0x69, 0x67, 0x68, 0x74, 0x04, 0x78, 0x01
		});
		MetadataAmf3 metadata = new MetadataAmf3(new RtmpHeader(MessageType.METADATA_AMF3), buffer);
		System.out.println(metadata);
	}
}
