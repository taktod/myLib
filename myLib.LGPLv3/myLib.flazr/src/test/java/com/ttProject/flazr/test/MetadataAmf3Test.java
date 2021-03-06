/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.test;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.message.MessageType;
import com.ttProject.flazr.rtmp.message.MetadataAmf3;
import com.ttProject.util.HexUtil;

public class MetadataAmf3Test {
	private Logger logger = LoggerFactory.getLogger(MetadataAmf3Test.class);
	/**
	 * Amf3の動作テストを実施します。
	 */
	@Test
	public void test() {
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(HexUtil.makeBuffer("0002000A6F6E4D65746144617461110A0B010B776964746804812005696404000D686569676874047801"));
		MetadataAmf3 metadata = new MetadataAmf3(new RtmpHeader(MessageType.METADATA_AMF3), buffer);
		logger.info(metadata.toString());
	}
}
