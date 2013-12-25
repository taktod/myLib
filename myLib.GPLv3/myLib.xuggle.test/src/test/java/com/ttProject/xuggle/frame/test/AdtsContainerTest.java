package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.adts.AdtsUnit;
import com.ttProject.container.adts.AdtsUnitReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * adtsコンテナのデコード動作テスト
 * @author taktod
 */
public class AdtsContainerTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(AdtsContainerTest.class);
	@Test
	public void aac() throws Exception {
		logger.info("aacテスト");
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("aac.aac")
			)
		);
	}
	private void decodeTest(IFileReadChannel source) {
		DecodeBase base = new DecodeBase();
		try {
			IReader reader = new AdtsUnitReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof AdtsUnit) {
					AdtsUnit adtsUnit = (AdtsUnit) container;
					logger.info(adtsUnit.getFrame());
					base.processAudioDecode(adtsUnit.getFrame());
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
