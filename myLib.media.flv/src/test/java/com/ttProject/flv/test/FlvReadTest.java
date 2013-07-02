package com.ttProject.flv.test;

import java.nio.ByteBuffer;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.tag.MetaTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

public class FlvReadTest {
//	@Test
	public void readTest() throws Exception {
		System.out.println("flvのデータを読み込むテスト");
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
			Thread.currentThread().getContextClassLoader().getResource("target.flv")
		);
		FlvHeader flvHeader = new FlvHeader();
		flvHeader.analyze(source);
		// タグを読み込んでいく。
		int position = source.position();
		ByteBuffer buffer = BufferUtil.safeRead(source, 11);
		switch(buffer.get()) {
		case 0x08:
			break;
		case 0x09:
			break;
		case 0x12:
			int size = (buffer.get() << 16) + (buffer.get() << 8) + buffer.get();
			int timestamp = (buffer.get() << 16) + (buffer.get() << 8) + buffer.get() + (buffer.get() << 24);
			MetaTag metaTag = new MetaTag(size, position, timestamp);
			metaTag.analyze(source);
			break;
		default:
			throw new Exception("解析できないデータでした。");
		}
	}
}
