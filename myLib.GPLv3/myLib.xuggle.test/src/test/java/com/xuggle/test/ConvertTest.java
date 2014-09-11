package com.xuggle.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.IWriter;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.container.mkv.MkvTagWriter;
import com.ttProject.frame.CodecType;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * xuggleによるデータのコンバート動作テスト
 * @author taktod
 */
public class ConvertTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(ConvertTest.class);
	/**
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("変換動作テスト");
		IReadChannel source = FileReadChannel.openFileReadChannel(
				"http://49.212.39.17/mario.flv"
		);
		IReader reader = new FlvTagReader();
		IWriter writer = new MkvTagWriter("output.mkv");
		writer.prepareHeader(CodecType.H264, CodecType.AAC);
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			if(container instanceof VideoTag) {
				VideoTag vTag = (VideoTag)container;
				writer.addFrame(0x09, vTag.getFrame());
			}
			else if(container instanceof AudioTag) {
				AudioTag aTag = (AudioTag)container;
				writer.addFrame(0x08, aTag.getFrame());
			}
		}
		writer.prepareTailer();
		logger.info("処理おわり");
	}
}
