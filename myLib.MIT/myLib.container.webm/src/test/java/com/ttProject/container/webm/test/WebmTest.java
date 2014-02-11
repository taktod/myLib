package com.ttProject.container.webm.test;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * webmの動作テスト
 * @author taktod
 */
public class WebmTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(WebmTest.class);
	/**
	 * analyerの動作テスト
	 */
//	@Test
	public void analyzerTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.webm")
			);
			IReader reader = new MkvTagReader();
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
