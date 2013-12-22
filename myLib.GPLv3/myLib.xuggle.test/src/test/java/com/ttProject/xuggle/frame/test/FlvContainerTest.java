package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.flv.FlvTagAnalyzer;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * flvコンテナのデコード動作テスト
 * @author taktod
 */
public class FlvContainerTest {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(FlvContainerTest.class);
//	@Test
	public void flv1Test() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("flv1.flv")
			)
		);
	}
//	@Test
	public void mp3Test() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mp3.flv")
			)
		);
	}
	@Test
	public void vp6Test() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					"http://red5.googlecode.com/svn-history/r4071/java/example/trunk/oflaDemo/www/streams/toystory3-vp6.flv"
			)
		);
	}
	private void decodeTest(IFileReadChannel source) {
		DecodeBase base = new DecodeBase();
		try {
			IAnalyzer analyzer = new FlvTagAnalyzer();
			IUnit unit = null;
			while((unit = analyzer.analyze(source)) != null) {
				if(unit instanceof VideoTag) {
					VideoTag vTag = (VideoTag)unit;
					logger.info(vTag.getFrame());
					base.processVideoDecode(vTag.getFrame());
				}
/*				else if(unit instanceof AudioTag) {
					AudioTag aTag = (AudioTag)unit;
					logger.info(aTag.getFrame());
					base.processAudioDecode(aTag.getFrame());
				}// */
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
