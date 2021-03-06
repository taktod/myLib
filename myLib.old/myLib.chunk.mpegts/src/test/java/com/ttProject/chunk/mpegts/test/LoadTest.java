/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.mpegts.test;

import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.IMediaChunkManager;
import com.ttProject.chunk.mpegts.MpegtsChunkManager;
import com.ttProject.chunk.mpegts.analyzer.MpegtsPesAnalyzer;
import com.ttProject.media.mpegts.IPacketAnalyzer;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.PacketAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * データを読み込む動作テスト
 * @author taktod
 */
public class LoadTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(LoadTest.class);
	/**
	 * 通常のmpegtsを分割する動作テスト
	 */
//	@Test
	public void analyzeNormalData() {
		System.out.println("#EXTM3U");
		System.out.println("#EXT-X-ALLOW-CACHE:NO");
		System.out.println("#EXT-X-TARGETDURATION:5");
		System.out.println("#EXT-X-VERSION:3");
		int counter = 0;
		IReadChannel source = null;
		FileOutputStream fos = null;
		try {
			// データソース
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.ts")
			);
			IMediaChunkManager chunkManager = new MpegtsChunkManager();
			// mpegtsのデータを投入するので、analyzerを設定しておく。
			((MpegtsChunkManager)chunkManager).addPesAnalyzer(new MpegtsPesAnalyzer());
			chunkManager.setDuration(5);
			IPacketAnalyzer analyzer = new PacketAnalyzer();
			Packet packet = null;
			IMediaChunk chunk = null;
			while((packet = analyzer.analyze(source)) != null) {
				// 見つけたpacketを順にmpegtsChunkManagerに流していけばOK
				chunk = chunkManager.getChunk(packet);
				if(chunk != null) {
					System.out.println("#EXTINF:" + chunk.getDuration());
					fos = new FileOutputStream("rtypeDelta_" + (++ counter) + ".ts");
					System.out.println("rtypeDelta_" + counter + ".ts");
					fos.getChannel().write(chunk.getRawBuffer());
					fos.close();
//					logger.info(chunk);
				}
			}
		}
		catch(Exception e) {
			logger.error("例外", e);
			Assert.fail("例外が発生しました。");
		}
		finally {
			if(fos != null) {
				try {
					fos.close();
				}
				catch(Exception e) {}
				fos = null;
			}
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
		}
	}
	/**
	 * 映像なしのmpegtsを分割する動作テスト
	 */
//	@Test
	public void analyzeNoVideoData() {
		IReadChannel source = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("mario.nv.ts");
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.novideo.ts")
			);
			IMediaChunkManager chunkManager = new MpegtsChunkManager();
			// mpegtsのデータを投入するので、analyzerを設定しておく。
			((MpegtsChunkManager)chunkManager).addPesAnalyzer(new MpegtsPesAnalyzer());
			IPacketAnalyzer analyzer = new PacketAnalyzer();
			Packet packet = null;
			IMediaChunk chunk = null;
			while((packet = analyzer.analyze(source)) != null) {
				// 見つけたpacketを順にmpegtsChunkManagerに流していけばOK
				chunk = chunkManager.getChunk(packet);
				if(chunk != null) {
					fos.getChannel().write(chunk.getRawBuffer());
					logger.info(chunk);
				}
			}
			chunk = chunkManager.close();
			if(chunk != null) {
				// TODO 終端の調整は実施していません。
				fos.getChannel().write(chunk.getRawBuffer());
				logger.info(chunk);
			}
		}
		catch(Exception e) {
			logger.error("例外", e);
			Assert.fail("例外が発生しました。");
		}
		finally {
			if(fos != null) {
				try {
					fos.close();
				}
				catch(Exception e) {}
				fos = null;
			}
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {}
				source = null;
			}
		}
	}
	/**
	 * 音声なしのmpegtsを分割する動作テスト
	 */
//	@Test
	public void analyzeNoAudioData() {
		IReadChannel source = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("mario.na.ts");
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.noaudio.ts")
			);
			IMediaChunkManager chunkManager = new MpegtsChunkManager();
			// mpegtsのデータを投入するので、analyzerを設定しておく。
			((MpegtsChunkManager)chunkManager).addPesAnalyzer(new MpegtsPesAnalyzer());
			IPacketAnalyzer analyzer = new PacketAnalyzer();
			Packet packet = null;
			IMediaChunk chunk = null;
			while((packet = analyzer.analyze(source)) != null) {
				// 見つけたpacketを順にmpegtsChunkManagerに流していけばOK
				chunk = chunkManager.getChunk(packet);
				if(chunk != null) {
					fos.getChannel().write(chunk.getRawBuffer());
					logger.info(chunk);
				}
			}
		}
		catch(Exception e) {
			logger.error("例外", e);
			Assert.fail("例外が発生しました。");
		}
		finally {
			if(fos != null) {
				try {
					fos.close();
				}
				catch(Exception e) {}
				fos = null;
			}
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {}
				source = null;
			}
		}
	}
}
