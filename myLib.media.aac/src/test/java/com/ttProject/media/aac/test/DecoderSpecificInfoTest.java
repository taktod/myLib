package com.ttProject.media.aac.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.aac.DecoderSpecificInfo;
import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.IFrameAnalyzer;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

public class DecoderSpecificInfoTest {
	private Logger logger = Logger.getLogger(DecoderSpecificInfoTest.class);
	@Test
	public void restore() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("1210"));
		DecoderSpecificInfo specificInfo = new DecoderSpecificInfo();
		specificInfo.analyze(channel);
		logger.info(specificInfo);
	}
	@Test
	public void make() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.aac")
		);
		IFrameAnalyzer analyzer = new FrameAnalyzer();
		DecoderSpecificInfo specificInfo = new DecoderSpecificInfo();
		Frame frame = null;
		while((frame = analyzer.analyze(source)) != null) {
			logger.info(frame);
			if(frame instanceof Aac) {
				specificInfo.analyze((Aac)frame);
				logger.info(HexUtil.toHex(specificInfo.getInfoBuffer()));
				logger.info(specificInfo);
				break;
			}
		}
		source.close();
	}
}
