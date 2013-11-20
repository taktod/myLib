package com.ttProject.media.mp3.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.FrameAnalyzer;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.media.mp3.Mp3Manager;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * mp3の解析テスト
 * @author taktod
 */
public class FileAnalyzeTest {
	private Logger logger = Logger.getLogger(FileAnalyzeTest.class);
	/**
	 * ファイルサイズが固定されている状態での動作テスト
	 */
	@Test
	public void fixedFileTest() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.mp3")
		);
		IFrameAnalyzer analyzer = new FrameAnalyzer();
		// sourceをそのまま解析します。
		Frame frame = null;
		while((frame = analyzer.analyze(source)) != null) {
			logger.info(frame);
		}
		// 最後までいったらおわり
		source.close();
	}
	/**
	 * ファイルサイズはわからないがbyteBufferデータが順番に追加される状態での動作テスト
	 * stdinみたいにサイズがわかっていないデータはこちら側
	 */
	@Test
	public void appendingBufferTest() throws Exception {
		logger.info("追記動作のテスト開始");
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.mp3")
		);
		// 適当な量ずつデータを取り出します。
		ByteBuffer buffer = BufferUtil.safeRead(source, 2560);
		Mp3Manager manager = new Mp3Manager();
		// 解析にまわす。
		for(Frame tag : manager.getUnits(buffer)) {
			logger.info(tag);
		}
		// 続きのデータを読み込む
		buffer = BufferUtil.safeRead(source, 2560);
		// 解析にまわす
		for(Frame tag : manager.getUnits(buffer)) {
			logger.info(tag);
		}
		// さらにつづける
		buffer = BufferUtil.safeRead(source, 2560);
		for(Frame tag : manager.getUnits(buffer)) {
			logger.info(tag);
		}
		// 飽きたらやめる
		source.close();
	}
}
