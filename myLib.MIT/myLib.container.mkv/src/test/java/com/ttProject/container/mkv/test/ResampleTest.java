package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mkv.test.resampler.AudioResampler;

/**
 * wav resample test.
 * @author taktod
 */
public class ResampleTest {
	private Logger logger = Logger.getLogger(ResampleTest.class);
	@Test
	public void test() throws Exception {
		logger.info("start test.");
		AudioResampler resampler = new AudioResampler(1, 22050, 44100);
		logger.info("test end.");
		Long test = new Long(0);
		long[] tList = new long[1];
		testMethod(tList);
		test = tList[0];
		logger.info(test);
	}
	public void testMethod(long[] test) {
		test[0] = 135;
	}
}
