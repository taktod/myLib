/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.transcode.xuggle.flv.test;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.Unit;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.transcode.ITrackListener;
import com.ttProject.transcode.xuggle.IXuggleTranscodeManager;
import com.ttProject.transcode.xuggle.Preset;
import com.ttProject.transcode.xuggle.XuggleTranscodeManager;
import com.ttProject.transcode.xuggle.packet.FlvAudioPacketizer;
import com.ttProject.transcode.xuggle.packet.FlvDepacketizer;
import com.ttProject.transcode.xuggle.packet.FlvVideoPacketizer;
import com.ttProject.transcode.xuggle.track.IXuggleTrackManager;

/**
 * ファイルをxuggleで変換する動作テスト
 * @author taktod
 */
public class FileTranscodeTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FileTranscodeTest.class);
//	@Test
	public void checkCpu() {
		System.out.println(Runtime.getRuntime().availableProcessors());
	}
	/**
	 * 動作テスト
	 */
	@Test
	public void test() {
//		JNIMemoryManager.setMemoryModel(MemoryModel.NATIVE_BUFFERS);
		IFileReadChannel source = null;
		IXuggleTranscodeManager audioTranscodeManager = null;
		IXuggleTranscodeManager videoTranscodeManager = null;
		try {
			// mario.flvをダウンロードしつつコンバートさせる
			source = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.flv");
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.analyze(source);
			ITagAnalyzer analyzer = new TagAnalyzer();
			Tag tag = null;
			
			// xuggleに変換させる。
			audioTranscodeManager = new XuggleTranscodeManager();
			videoTranscodeManager = new XuggleTranscodeManager();
			audioTranscodeManager.setPacketizer(new FlvAudioPacketizer());
			videoTranscodeManager.setPacketizer(new FlvVideoPacketizer());

			// 音声の設定
			IXuggleTrackManager trackManager = (IXuggleTrackManager) audioTranscodeManager.addNewTrackManager();
			trackManager.setDepacketizer(new FlvDepacketizer()); // flvTagにする
			trackManager.setEncoder(Preset.mp3()); // mp3
			trackManager.setTrackListener(new ITrackListener() {
				@Override
				public void receiveData(List<Unit> units) {
					logger.info(units);
				}
				@Override
				public void close() {
					logger.info("終了通知を受け取りました。");
				}
			});

			// 音声の設定
			trackManager = (IXuggleTrackManager) videoTranscodeManager.addNewTrackManager();
			trackManager.setDepacketizer(new FlvDepacketizer()); // flvTagにする
			trackManager.setEncoder(Preset.h264()); // h264
			trackManager.setTrackListener(new ITrackListener() {
				@Override
				public void receiveData(List<Unit> units) {
					logger.info(units);
				}
				@Override
				public void close() {
					logger.info("終了通知を受け取りました");
				}
			});
			// 処理実行
			while((tag = analyzer.analyze(source)) != null) {
				audioTranscodeManager.transcode(tag);
				videoTranscodeManager.transcode(tag);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(audioTranscodeManager != null) {
				audioTranscodeManager.close();
				audioTranscodeManager = null;
			}
			if(videoTranscodeManager != null) {
				videoTranscodeManager.close();
				videoTranscodeManager = null;
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
