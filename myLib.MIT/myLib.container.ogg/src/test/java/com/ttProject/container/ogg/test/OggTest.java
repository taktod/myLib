package com.ttProject.container.ogg.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.ogg.OggPageReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * oggファイルの読み込み動作テスト
 * @author taktod
 */
public class OggTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(OggTest.class);
	/**
	 * 解析動作テスト
	 */
	@Test
	public void analyzerTest() {
		logger.info("ogg解析テスト");
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.speex.ogg")
			);
			IReader reader = new OggPageReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
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
	/**
	 * speexのmetaデータが非常にながい場合にどうなるか・・・というテストとしてつくったもの。あとでサーバー上のoggファイルは撤去する予定。
	 */
//	@Test
	public void analyzeTest2() {
		logger.info("ogg解析テスト2");
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/mario.speex2.ogg"
			);
			IReader reader = new OggPageReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
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
