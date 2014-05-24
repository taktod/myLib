/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.ffmpeg.flv.test;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.ttProject.media.Unit;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.transcode.IExceptionListener;
import com.ttProject.transcode.ITrackListener;
import com.ttProject.transcode.ffmpeg.FfmpegTranscodeManager;
import com.ttProject.transcode.ffmpeg.IFfmpegTranscodeManager;
import com.ttProject.transcode.ffmpeg.track.FlvAudioUnitSelector;
import com.ttProject.transcode.ffmpeg.track.FlvVideoUnitSelector;
import com.ttProject.transcode.ffmpeg.track.IFfmpegTrackManager;
import com.ttProject.transcode.ffmpeg.unit.FlvDeunitizer;
import com.ttProject.transcode.ffmpeg.unit.FlvUnitizer;

/**
 * ファイルをffmpegで変換する動作テスト
 * @author taktod
 */
public class FileTranscodeTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FileTranscodeTest.class);
	/**
	 * 動作テスト
	 */
//	@Test
	public void test() {
		IFileReadChannel source = null;
		IFfmpegTranscodeManager transcodeManager = null;
		try {
			// リモートにあるflvの読み込み準備
			source = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.flv");
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.analyze(source);
			ITagAnalyzer analyzer = new TagAnalyzer();
			Tag tag = null;
			
			transcodeManager = new FfmpegTranscodeManager();
			logger.info("registerCommandするよん");
			transcodeManager.registerCommand("avconv -i - -f flv - 2>avconv.log");
			transcodeManager.setExceptionListener(new IExceptionListener() {
				@Override
				public void exceptionCaught(Exception e) {
					logger.error("例外が発生", e);
					Assert.fail("例外が発生");
				}
			});
			transcodeManager.setUnitizer(new FlvUnitizer());
			transcodeManager.setDeunitizer(new FlvDeunitizer());
			// 応答トラックを追加します。
			IFfmpegTrackManager trackManager = (IFfmpegTrackManager)transcodeManager.addNewTrackManager();
			trackManager.setUnitSelector(new FlvVideoUnitSelector());
			trackManager.setTrackListener(new ITrackListener() {
				@Override
				public void receiveData(List<Unit> units) {
					logger.info(units);
				}
				
				@Override
				public void close() {
					logger.info("a終了");
				}
			});
			trackManager = (IFfmpegTrackManager)transcodeManager.addNewTrackManager();
			trackManager.setUnitSelector(new FlvAudioUnitSelector());
			trackManager.setTrackListener(new ITrackListener() {
				@Override
				public void receiveData(List<Unit> units) {
					logger.info(units);
				}
				@Override
				public void close() {
					logger.info("b終了");
				}
			});
			
			// データを変換マネージャーに流し込んで動作させる。
			while((tag = analyzer.analyze(source)) != null) {
				// データを送りつける。
				transcodeManager.transcode(tag);
			}
			((FfmpegTranscodeManager)transcodeManager).waitForEnd();
		}
		catch(Exception e) {
			logger.warn("例外が発生した。", e);
		}
		finally {
			if(transcodeManager != null) {
				transcodeManager.close();
				transcodeManager = null;
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
