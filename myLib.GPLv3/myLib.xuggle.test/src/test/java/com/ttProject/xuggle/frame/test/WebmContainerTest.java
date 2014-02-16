package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.mkv.type.SimpleBlock;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * webmコンテナのデコード動作テスト
 * @author taktod
 */
public class WebmContainerTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(WebmContainerTest.class);
	@Test
	public void vp8() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("vp8.webm")
			)
		);
	}
	@Test
	public void vorbis() throws Exception {
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("vorbis.webm")
			)
		);
	}
	private void decodeTest(IFileReadChannel source) {
		DecodeBase base = new DecodeBase();
		try {
			IReader reader = new MkvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof SimpleBlock) {
					SimpleBlock simpleBlock = (SimpleBlock)container;
					IFrame frame = simpleBlock.getFrame();
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
