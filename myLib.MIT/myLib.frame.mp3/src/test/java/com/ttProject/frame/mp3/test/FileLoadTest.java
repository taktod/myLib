package com.ttProject.frame.mp3.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.mp3.Mp3FrameAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * ファイル読み込み関連動作テスト
 * @author taktod
 */
public class FileLoadTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FileLoadTest.class);
	/**
	 * analyzerの動作テスト
	 */
	@Test
	public void analyzerTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.mp3").toURI().toURL()
			);
			IAnalyzer analyzer = new Mp3FrameAnalyzer();
			IUnit unit = null;
			while((unit = analyzer.analyze(source)) != null) {
				logger.info(unit);
			}
		}
		catch(Exception e) {
			logger.warn("例外発生", e);
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
