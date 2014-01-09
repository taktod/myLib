package com.ttProject.container.mpegts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mpegts.MpegtsPacketReader;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mpegtsの動作テスト
 * @author taktod
 */
public class MpegtsTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsTest.class);
	@Test
	public void test() throws Exception {
		logger.info("test");
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.ts")
			)
		);
	}
	private void analyzerTest(IFileReadChannel source) {
		try {
			IReader reader = new MpegtsPacketReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof Pes) {
					// 再終端まできていないとデータがきません。
					if(((Pes) container).getFrame() != null) {
						logger.info(((Pes) container).getFrame());
					}
				}
//				logger.info(container);
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
