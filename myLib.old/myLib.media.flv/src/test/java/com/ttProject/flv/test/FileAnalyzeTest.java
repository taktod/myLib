/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.flv.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.FlvManager;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvの解析テスト
 * @author taktod
 */
public class FileAnalyzeTest {
	private Logger logger = Logger.getLogger(FileAnalyzeTest.class);
	/**
	 * ファイル全体を解析するテスト
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
		while((tag = analyzer.analyze(source)) != null) {
			logger.info(tag);
		}
		source.close();
	}
	/**
	 * サイズがわかっていないデータを順に受け取るときにflvを解析する動作テスト
	 */
//	@Test
	public void appendingBufferTest() throws Exception {
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.flv")
		);
		FlvHeader flvheader = new FlvHeader();
		flvheader.analyze(source);
		logger.info(flvheader);
		ByteBuffer buffer = BufferUtil.safeRead(source, 2560);
		FlvManager manager = new FlvManager();
		for(Tag tag : manager.getUnits(buffer)) {
			logger.info(tag);
		}
		buffer = BufferUtil.safeRead(source, 2560);
		for(Tag tag : manager.getUnits(buffer)) {
			logger.info(tag);
		}
		source.close();
	}
}
