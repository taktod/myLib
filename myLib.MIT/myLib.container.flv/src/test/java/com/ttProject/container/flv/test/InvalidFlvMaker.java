package com.ttProject.container.flv.test;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.flv.FlvHeaderTag;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * エラーのあるflvをつくってどうなるか試してみる
 * @author taktod
 */
public class InvalidFlvMaker {
	/** ロガー */
	private Logger logger = Logger.getLogger(InvalidFlvMaker.class);
	/**
	 * 音声の変なファイル作成
	 * @throws Exception
	 */
	@Test
	public void audio() throws Exception {
		logger.info("InvalidFlvをつくります。");
		IFileReadChannel source = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.flv");
		FlvTagReader reader = new FlvTagReader();
		FileChannel audioOutput0 = new FileOutputStream("audioOutput0.flv").getChannel(); // 通常
		FileChannel audioOutput1 = new FileOutputStream("audioOutput1.flv").getChannel(); // 開始位置がずれてる(20秒)
		FileChannel audioOutput2 = new FileOutputStream("audioOutput2.flv").getChannel(); // 20秒目から40秒目までデータがぬけてる
		FlvHeaderTag headerTag = new FlvHeaderTag();
		headerTag.setAudioFlag(true);
		headerTag.setVideoFlag(false);
		audioOutput0.write(headerTag.getData());
		audioOutput1.write(headerTag.getData());
		audioOutput2.write(headerTag.getData());
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			logger.info("info:" + container.toString());
			if(container instanceof AudioTag) {
				AudioTag aTag = (AudioTag)container;
				audioOutput0.write(aTag.getData());
				if(aTag.getPts() < 20000 || aTag.getPts() > 40000) {
					audioOutput2.write(aTag.getData());
				}
				aTag.setPts(aTag.getPts() + 20000);
				audioOutput1.write(aTag.getData());
			}
		}
		logger.info("処理おわり");
		source.close();
		audioOutput0.close();
		audioOutput1.close();
		audioOutput2.close();
	}
}
