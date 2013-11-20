package com.ttProject.media.mp4.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.AtomAnalyzer;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * ファイルの読み込みテスト
 * @author taktod
 *
 */
public class FileAnalyzeTest {
	private Logger logger = Logger.getLogger(FileAnalyzeTest.class);
	/**
	 * 固定ファイルの読み込みテスト
	 */
	@Test
	public void fixedFileTest() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.mp4")
		);
		IAtomAnalyzer analyzer = new AtomAnalyzer();
		Atom atom = null;
		while((atom = analyzer.analyze(source)) != null) {
			logger.info(atom);
		}
		source.close();
	}
	/**
	 * 追記されているデータ読み込みテスト(mp4ではありえないので、やらない(moofのあるデータならあり得るかも))
	 */
	@Test
	public void appendingBufferTest() throws Exception {
		logger.error("mp4に関しては追記動作がありえないので、このテストはなしです。");
	}
}
