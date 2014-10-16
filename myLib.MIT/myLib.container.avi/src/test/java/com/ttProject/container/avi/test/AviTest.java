package com.ttProject.container.avi.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.riff.RiffFrameUnit;
import com.ttProject.container.riff.RiffUnitReader;
import com.ttProject.frame.IFrame;
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
//					Thread.currentThread().getContextClassLoader().getResource("test.adpcm_ima_wav.avi")
					Thread.currentThread().getContextClassLoader().getResource("test.mjpegadpcm_ima_wav.avi")
//					Thread.currentThread().getContextClassLoader().getResource("test.mjpeg.avi")
			);
			IReader reader = new RiffUnitReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof RiffFrameUnit) {
					IFrame frame = ((RiffFrameUnit) container).getFrame();
					if(frame != null) {
						logger.info(frame);
						logger.info(1f * frame.getPts() / frame.getTimebase());
					}
				}
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
