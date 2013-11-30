package com.ttProject.media.flv1.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * fileデータを読み込む動作テスト
 * @author taktod
 */
public class FileAnalyzeTest {
	private Logger logger = Logger.getLogger(FileAnalyzeTest.class);
	/**
	 * ファイルを解析するテスト
	 * @throws Exception
	 */
	@Test
	public void fixedFileTest() throws Exception {
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.flv")
		);
		FlvHeader flvheader = new FlvHeader();
		flvheader.analyze(source);
		logger.info(flvheader);
		ITagAnalyzer analyzer = new TagAnalyzer();
		// sourceをそのまま解析する。
		Tag tag = null;
		while((tag = analyzer.analyze(source)) != null) {
			logger.info(tag);
			// h263のデータを拾うところまできたので、ここから内部データを解析して、frameをつくる必要あり。
		}
		source.close();
	}
}
