package com.ttProject.chunk.mpegts.test;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

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
	private Logger logger = Logger.getLogger(LoadTest.class);
//	@Test
	public void analyzeNormalData() {
		IReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mario.ts")
			);
			IPacketAnalyzer analyzer = new PacketAnalyzer();
			Packet packet = null;
			while((packet = analyzer.analyze(source)) != null) {
				System.out.println(packet);
			}
		}
		catch(Exception e) {
			logger.error("例外", e);
			Assert.fail("例外が発生しました。");
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {}
				source = null;
			}
		}
	}
	@Test
	public void analyzeNoVideoData() {
		IReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mario_novideo.ts")
			);
			IPacketAnalyzer analyzer = new PacketAnalyzer();
			Packet packet = null;
			while((packet = analyzer.analyze(source)) != null) {
				System.out.println(packet.getClass().getSimpleName());
			}
		}
		catch(Exception e) {
			logger.error("例外", e);
			Assert.fail("例外が発生しました。");
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {}
				source = null;
			}
		}
	}
//	@Test
	public void analyzeNoAudioData() {
		IReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mario_noaudio.ts")
			);
			IPacketAnalyzer analyzer = new PacketAnalyzer();
			Packet packet = null;
			while((packet = analyzer.analyze(source)) != null) {
				System.out.println(packet);
			}
		}
		catch(Exception e) {
			logger.error("例外", e);
			Assert.fail("例外が発生しました。");
		}
		finally {
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
