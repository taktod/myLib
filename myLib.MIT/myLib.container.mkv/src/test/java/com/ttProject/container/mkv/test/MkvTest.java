package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.mkv.type.BlockGroup;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mkvの動作テスト
 * @author taktod
 */
public class MkvTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvTest.class);
	/**
	 * analyzerの動作テスト
	 */
	@Test
	public void analyzerTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.mkv")
			);
			IReader reader = new MkvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof BlockGroup) {
					logger.info(container);
					for(MkvTag tag : ((BlockGroup)container).getChildList()) {
						if(tag instanceof MkvBlockTag) {
							MkvBlockTag blockTag = (MkvBlockTag)tag;
							logger.info(blockTag);
							logger.info(blockTag.getFrame());
						}
					}
				}
				if(container instanceof MkvBlockTag) {
					MkvBlockTag blockTag = (MkvBlockTag)container;
					logger.info(blockTag);
					logger.info(blockTag.getFrame());
				}
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
