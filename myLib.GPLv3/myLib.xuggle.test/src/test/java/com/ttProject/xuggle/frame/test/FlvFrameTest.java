package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.flv.FlvTagAnalyzer;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;
import com.ttProject.xuggle.frame.XuggleHelper;

/**
 * xuggleによるコンテナの変換を実行するテスト
 * @author taktod
 */
public class FlvFrameTest {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(FlvFrameTest.class);
	@Test
	public void flv1Test() throws Exception {
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("flv1.flv")
			)
		);
	}
	private void convertTest(IFileReadChannel source) {
		try {
			IAnalyzer analyzer = new FlvTagAnalyzer();
			IUnit unit = null;
			while((unit = analyzer.analyze(source)) != null) {
				if(unit instanceof VideoTag) {
					VideoTag vTag = (VideoTag)unit;
					logger.info(vTag.getFrame());
					XuggleHelper.getPackets(vTag.getFrame());
				}
			}
		}
		catch(Exception e) {
			logger.error("例外発生", e);
		}
		finally {
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
