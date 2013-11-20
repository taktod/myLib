package com.ttProject.media.aac.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.IFrameAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * aacの解析テスト
 * @author taktod
 */
public class FileAnalyzeTest {
	private Logger logger = Logger.getLogger(FileAnalyzeTest.class);
	@Test
	public void fixedFileTest() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.aac")
		);
		IFrameAnalyzer analyzer = new FrameAnalyzer();
		int counter = 0;
		Frame frame = null;
		while((frame = analyzer.analyze(source)) != null) {
			logger.info(frame);
			counter ++;
		}
		logger.info((counter *1.024/ 44.1f));
		source.close();
	}
}
