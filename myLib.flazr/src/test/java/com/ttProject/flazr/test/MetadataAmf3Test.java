package com.ttProject.flazr.test;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.message.MessageType;
import com.ttProject.flazr.rtmp.message.MetadataAmf3;
import com.ttProject.util.HexUtil;

public class MetadataAmf3Test {
	/**
	 * Amf3の動作テストを実施します。
	 */
	@Test
	public void test() {
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(HexUtil.makeBuffer("0002000A6F6E4D65746144617461110A0B010B776964746804812005696404000D686569676874047801"));
		MetadataAmf3 metadata = new MetadataAmf3(new RtmpHeader(MessageType.METADATA_AMF3), buffer);
		System.out.println(metadata);
	}
}
