package com.ttProject.container.adts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.adts.AdtsUnitReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * adtsの読み込み動作テスト
 * @author taktod
 */
public class AdtsTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(AdtsTest.class);
	/**
	 * 解析動作テスト
	 */
	@Test
	public void analyzeTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.aac.aac")
			);
			IReader reader = new AdtsUnitReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
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
