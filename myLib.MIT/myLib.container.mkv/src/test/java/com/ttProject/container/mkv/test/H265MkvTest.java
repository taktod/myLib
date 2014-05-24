package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.mkv.type.SimpleBlock;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * h265(mkv)の動作テスト
 * @author taktod
 */
public class H265MkvTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(H265MkvTest.class);
	/**
	 * analyzerの動作テスト
	 */
	@Test
	public void analyzerTest() {
		IFileReadChannel source = null;
		int lastPosition = 0;
		try {
			source = FileReadChannel.openFileReadChannel(
					""
			);
			IReader reader = new MkvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof SimpleBlock) {
					SimpleBlock blockTag = (SimpleBlock) container;
					logger.info("key:" + blockTag.isKeyFrame() + " invisible:" + blockTag.isInvisibleFrame());
					if(blockTag.getFrame() instanceof VideoFrame) {
						VideoFrame vFrame = (VideoFrame)blockTag.getFrame();
						logger.info(vFrame.getClass() + ":" + vFrame.getWidth() + "x" + vFrame.getHeight());
					}
					else if(blockTag.getFrame() instanceof AudioFrame) {
						AudioFrame aFrame = (AudioFrame)blockTag.getFrame();
						logger.info(aFrame.getClass() + ":" + aFrame.getSampleRate() + ":" + aFrame.getChannel());
					}
				}
				lastPosition = source.position();
			}
		}
		catch(Exception e) {
			logger.warn("例外発生", e);
			try {
				logger.warn("エラー発生場所:" + Integer.toHexString(lastPosition));
			}
			catch(Exception ex) {
				
			}
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
