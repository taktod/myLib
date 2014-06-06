/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.mp3.test;

import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.IMediaChunkManager;
import com.ttProject.chunk.mp3.Mp3ChunkManager;
import com.ttProject.chunk.mp3.analyzer.Mp3FrameAnalyzer;
import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.FrameAnalyzer;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 読み込み動作テスト
 * @author taktod
 */
public class LoadTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(LoadTest.class);
	/**
	 * 通常の動作テスト
	 */
	@Test
	public void analyzeNormalData() {
		logger.info("通常のmp3のchunk作成テスト");
		IReadChannel source = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("test.mp3");
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.mp3")
			);
			IMediaChunkManager chunkManager = new Mp3ChunkManager();
			chunkManager.setDuration(5);
			((Mp3ChunkManager)chunkManager).addMp3FrameAnalyzer(new Mp3FrameAnalyzer());
			IFrameAnalyzer analyzer = new FrameAnalyzer();
			Frame frame = null;
			IMediaChunk chunk = null;
			logger.info("mp3の解析はじめるよ");
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
