package com.ttProject.humble.test;

import io.humble.video.AudioChannel.Layout;
import io.humble.video.AudioFormat.Type;
import io.humble.video.MediaAudio;
import io.humble.video.MediaAudioResampler;
import io.humble.video.Rational;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author taktod
 */
public class MutedSampleTest {
	/** logger */
	private Logger logger = Logger.getLogger(MutedSampleTest.class);
	@Test
	public void DBL() throws Exception {
		Type sourceType = Type.SAMPLE_FMT_DBL;
		logger.info("DBL -> DBLP");
		resampleTest(sourceType, Type.SAMPLE_FMT_DBLP);
	}
	@Test
	public void S16() throws Exception {
		Type sourceType = Type.SAMPLE_FMT_S16;
		logger.info("S16 -> S16P");
		resampleTest(sourceType, Type.SAMPLE_FMT_S16P);
	}
	private void resampleTest(Type sourceType, Type targetType) throws Exception {
		MediaAudio samples = MediaAudio.make(44100, 44100, 2, Layout.CH_LAYOUT_STEREO, sourceType);
		samples.setComplete(true);
		samples.setTimeStamp(0);
		samples.setTimeBase(Rational.make(1, 44100));
		MediaAudioResampler resampler = MediaAudioResampler.make(Layout.CH_LAYOUT_STEREO, 44100, targetType, Layout.CH_LAYOUT_STEREO, 44100, sourceType);
		resampler.open();
		MediaAudio result = MediaAudio.make(44100, 44100, 2, Layout.CH_LAYOUT_STEREO, targetType);
		int num = resampler.resampleAudio(result, samples);
		logger.info("res:" + num);
		logger.info(result);
	}
}
