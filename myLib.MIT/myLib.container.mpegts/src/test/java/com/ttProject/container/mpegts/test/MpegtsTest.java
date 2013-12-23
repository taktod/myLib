package com.ttProject.container.mpegts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mpegts.MpegtsPacketAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * mpegtsの動作テスト
 * @author taktod
 */
public class MpegtsTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsTest.class);
	@Test
	public void test() throws Exception {
		logger.info("test");
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.ts")
			)
		);
	}
	private void analyzerTest(IFileReadChannel source) {
		try {
			IAnalyzer analyzer = new MpegtsPacketAnalyzer();
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
