package com.xuggle.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * データのデコード処理を実施してみるテスト
 * @author taktod
 */
public class DecodeTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(DecodeTest.class);
	/**
	 * 動作テスト
	 */
	@Test
	public void test() {
		try {
			IReadChannel source = FileReadChannel.openFileReadChannel(
					"xuggle_sound.error.flv"
			);
			FlvTagReader reader = new FlvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof VideoTag) {
					logger.info(container);
				}
				else if(container instanceof AudioTag) {
					logger.info(container);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
