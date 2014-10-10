package com.ttProject.container.avi.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.riff.RiffUnitReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * avi load test
 * @author taktod
 */
public class AviTest {
	/** logger */
	private Logger logger = Logger.getLogger(AviTest.class);
	/**
	 * test
	 */
	@Test
	public void test() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.adpcm_ima_wav.avi")
//					Thread.currentThread().getContextClassLoader().getResource("test.mjpeg.avi")
			);
			IReader reader = new RiffUnitReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
			}
		}
		catch(Exception e) {
			logger.warn("", e);
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
		}
	}
}
