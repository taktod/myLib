package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mp3.Mp3Unit;
import com.ttProject.container.mp3.Mp3UnitAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * mp3コンテナのデコード動作テスト
 * @author taktod
 */
public class Mp3ContainerTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(Mp3ContainerTest.class);
	@Test
	public void mp3() throws Exception {
		logger.info("mp3テスト");
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mp3.mp3")
			)
		);
	}
	private void decodeTest(IFileReadChannel source) {
		DecodeBase base = new DecodeBase();
		try {
			IAnalyzer analyzer = new Mp3UnitAnalyzer();
			IUnit unit = null;
			while((unit = analyzer.analyze(source)) != null) {
				if(unit instanceof Mp3Unit) {
					Mp3Unit mp3Unit = (Mp3Unit) unit;
					logger.info(mp3Unit.getPosition());
					logger.info(mp3Unit.getFrame());
					base.processAudioDecode(mp3Unit.getFrame());
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
