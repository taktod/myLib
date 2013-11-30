package com.ttProject.media.vp6.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.media.vp6.Frame;
import com.ttProject.media.vp6.FrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

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
		FrameAnalyzer frameAnalyzer = new FrameAnalyzer();
		while((tag = analyzer.analyze(source)) != null) {
			// vp6のデータを拾うところまできたので、ここから内部データを解析して、frameをつくる必要あり。
			if(tag instanceof VideoTag) {
				VideoTag vTag = (VideoTag) tag;
				if(vTag.getCodec() == CodecType.ON2VP6) {
					logger.info(vTag);
					// 中身を解析する
					ByteBuffer buffer = vTag.getRawData();
					// vp6のデータはflvから取り出すときに先頭の1バイトを取り出して末端につける必要があり。
					ByteBuffer data = ByteBuffer.allocate(buffer.remaining());
					byte first = buffer.get();
					data.put(buffer);
					data.put(first);
					data.flip();
					IReadChannel dataChannel = new ByteReadChannel(data);
					Frame frame = frameAnalyzer.analyze(dataChannel);
					logger.info(frame);
				}
			}
		}
		source.close();
	}
}
