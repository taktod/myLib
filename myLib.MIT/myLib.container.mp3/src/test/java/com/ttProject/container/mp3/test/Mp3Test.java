package com.ttProject.container.mp3.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mp3.Mp3UnitAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * mp3データの読み込み動作テスト
 * @author taktod
 */
public class Mp3Test {
	/** ロガー */
	private Logger logger = Logger.getLogger(Mp3Test.class);
	@Test
	public void analyzeTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.mp3")
			);
			IAnalyzer analyzer = new Mp3UnitAnalyzer();
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
