package com.ttProject.container.ogg.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.ogg.OggPageAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * oggファイルの読み込み動作テスト
 * @author taktod
 */
public class OggTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(OggTest.class);
	/**
	 * 解析動作テスト
	 */
	@Test
	public void analyzerTest() {
		logger.info("ogg解析テスト");
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.vorbis.ogg")
			);
			IAnalyzer analyzer = new OggPageAnalyzer();
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
