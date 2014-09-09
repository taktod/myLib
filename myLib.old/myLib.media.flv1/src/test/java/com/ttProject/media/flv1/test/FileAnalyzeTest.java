/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.flv1.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.media.flv1.Frame;
import com.ttProject.media.flv1.FrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * fileデータを読み込む動作テスト
 * @author taktod
 */
public class FileAnalyzeTest {
	private Logger logger = Logger.getLogger(FileAnalyzeTest.class);
	/**
	 * ファイルを解析するテスト
	 * @throws Exception
	 */
//	@Test
	public void fixedFileTest() throws Exception {
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.flv")
		);
		FlvHeader flvheader = new FlvHeader();
		flvheader.analyze(source);
		logger.info(flvheader);
		ITagAnalyzer analyzer = new TagAnalyzer();
		// sourceをそのまま解析する。
		Tag tag = null;
		FrameAnalyzer frameAnalyzer = new FrameAnalyzer();
		while((tag = analyzer.analyze(source)) != null) {
			// h263のデータを拾うところまできたので、ここから内部データを解析して、frameをつくる必要あり。
			if(tag instanceof VideoTag) {
				VideoTag vTag = (VideoTag) tag;
				if(vTag.getCodec() == CodecType.H263) {
					logger.info(vTag);
					ByteBuffer buffer = vTag.getRawData();
					IReadChannel dataChannel = new ByteReadChannel(buffer);
					Frame frame = frameAnalyzer.analyze(dataChannel);
					logger.info(frame);
				}
			}
		}
		source.close();
	}
}
