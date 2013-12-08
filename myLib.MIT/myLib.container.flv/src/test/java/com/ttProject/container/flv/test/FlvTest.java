package com.ttProject.container.flv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.flv.FlvTagAnalyzer;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;
import com.ttProject.util.HexUtil;

/**
 * flvの動作テスト
 * @author taktod
 */
public class FlvTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvTest.class);
	/**
	 * analyzerの動作テスト
	 */
	@Test
	public void analyzerTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.flv").toURI().toURL()
			);
			IAnalyzer analyzer = new FlvTagAnalyzer();
			IUnit unit = null;
			while((unit = analyzer.analyze(source)) != null) {
				logger.info(unit);
				if(unit instanceof VideoTag) {
					VideoTag vTag = (VideoTag)unit;
					vTag.analyzeFrame();
				}
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
