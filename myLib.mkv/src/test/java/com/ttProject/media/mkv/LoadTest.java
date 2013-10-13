package com.ttProject.media.mkv;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * h.264のmshはcodecPrivateの中にはいっていた。
 * aacのmshもcodecPrivateの中にはいっていた。
 * @author taktod
 *
 */
public class LoadTest {
	private Logger logger = Logger.getLogger(LoadTest.class);
	@Test
	public void test() throws Exception {
		IReadChannel channel = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("testffmpeg.webm")
		);
		IElementAnalyzer analyzer = new ElementAnalyzer();
		// とりあえず中身をしる必要があるので、しっていく
		while(analyzer.analyze(channel) != null) {
		}
		logger.info("終了");
	}
}
