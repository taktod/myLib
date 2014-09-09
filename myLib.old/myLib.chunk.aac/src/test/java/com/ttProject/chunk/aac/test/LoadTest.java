/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.aac.test;

import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.IMediaChunkManager;
import com.ttProject.chunk.aac.AacChunkManager;
import com.ttProject.chunk.aac.analyzer.AacFrameAnalyzer;
import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.IFrameAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 動作テスト
 * @author taktod
 */
public class LoadTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(LoadTest.class);
	/**
	 * 通常の動作解析
	 */
//	@Test
	public void analyzeNormalData() {
		logger.info("通常のaacのchunk作成テスト");
		IReadChannel source = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("mario.aac");
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.aac")
			);
			IMediaChunkManager chunkManager = new AacChunkManager();
			chunkManager.setDuration(5);
			((AacChunkManager)chunkManager).addAacFrameAnalyzer(new AacFrameAnalyzer());
			IFrameAnalyzer analyzer = new FrameAnalyzer();
			Frame frame = null;
			IMediaChunk chunk = null;
			logger.info("aacの解析はじめるよ");
			while((frame = analyzer.analyze(source)) != null) {
				chunk = chunkManager.getChunk(frame);
				if(chunk != null) {
					fos.getChannel().write(chunk.getRawBuffer());
					logger.info(chunk);
				}
			}
		}
		catch(Exception e) {
			logger.warn("例外発生", e);
			Assert.fail("例外が発生しました。");
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
			if(fos != null) {
				try {
					fos.close();
				}
				catch(Exception e) {
				}
				fos = null;
			}
		}
	}
}
