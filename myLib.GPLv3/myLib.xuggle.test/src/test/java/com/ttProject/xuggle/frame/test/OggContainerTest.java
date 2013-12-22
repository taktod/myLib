package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.ogg.OggPage;
import com.ttProject.container.ogg.OggPageAnalyzer;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * oggコンテナのデコード動作テスト
 * @author taktod
 */
public class OggContainerTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(OggContainerTest.class);
	@Test
	public void speex() throws Exception {
		logger.info("speexテスト");
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("speex.ogg")
			)
		);
	}
	private void decodeTest(IFileReadChannel source) {
		DecodeBase base = new DecodeBase();
		try {
			IAnalyzer analyzer = new OggPageAnalyzer();
			IUnit unit = null;
			while((unit = analyzer.analyze(source)) != null) {
				if(unit instanceof OggPage) {
					OggPage page = (OggPage) unit;
					for(IFrame frame : page.getFrameList()) {
						logger.info(frame);
						if(frame instanceof IAudioFrame) {
							base.processAudioDecode((IAudioFrame)frame);
						}
						else if(frame instanceof IVideoFrame) {
							base.processVideoDecode((IVideoFrame)frame);
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("例外発生", e);
		}
		finally {
			if(base != null) {
				base.close();
				base = null;
			}
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e){}
				source = null;
			}
		}
	}
}
