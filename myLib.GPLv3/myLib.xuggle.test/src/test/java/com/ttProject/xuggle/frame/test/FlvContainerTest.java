package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * flvコンテナのデコード動作テスト
 * @author taktod
 */
public class FlvContainerTest {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(FlvContainerTest.class);
	@Test
	public void flv1Test() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("flv1.flv")
			)
		);
	}
	@Test
	public void mp3Test() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mp3.flv")
			)
		);
	}
	@Test
	public void vp6Test() throws Exception {
		// TODO このテストをする場合は、audioTagがちょっと邪魔
		decodeTest(
			FileReadChannel.openFileReadChannel(
					"http://red5.googlecode.com/svn-history/r4071/java/example/trunk/oflaDemo/www/streams/toystory3-vp6.flv"
			)
		);
	}
	@Test
	public void h264Test() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("h264.flv")
			)
		);
	}
	@Test
	public void aacTest() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("aac.flv")
			)
		);
	}
	@Test
	public void adpcmswfTest() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("adpcmswf.flv")
			)
		);
	}
	@Test
	public void nellymoserTest() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("nellymoser.flv")
			)
		);
	}
	@Test
	public void speexTest() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("speex.flv")
			)
		);
	}
	private void decodeTest(IFileReadChannel source) {
		DecodeBase base = new DecodeBase();
		try {
			IReader reader = new FlvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof VideoTag) {
					VideoTag vTag = (VideoTag)container;
					logger.info(vTag.getFrame());
					base.processVideoDecode(vTag.getFrame());
				}
				else if(container instanceof AudioTag) {
					AudioTag aTag = (AudioTag)container;
					logger.info(aTag.getFrame());
					base.processAudioDecode(aTag.getFrame());
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
