package com.ttProject.media.vp6.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

public class FileAnalyzeTest {
	private Logger logger = Logger.getLogger(FileAnalyzeTest.class);
	@Test
	public void test() throws Exception {
		IFileReadChannel source = FileReadChannel.openFileReadChannel("http://red5.googlecode.com/svn-history/r4071/java/example/trunk/oflaDemo/www/streams/toystory3-vp6.flv");
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
