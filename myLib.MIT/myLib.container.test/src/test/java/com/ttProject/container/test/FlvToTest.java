package com.ttProject.container.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.IWriter;
import com.ttProject.container.adts.AdtsUnitWriter;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.container.mp3.Mp3UnitWriter;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.HexUtil;

/**
 * flvを他のコンテナに変換する動作テスト
 * @author taktod
 */
public class FlvToTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvToTest.class);
	/**
	 * mp3にコンバートする
	 * @throws Exception
	 */
//	@Test
	public void mp3() throws Exception {
		logger.info("mp3に変換する動作テスト");
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mp3.flv")
			),
			new Mp3UnitWriter("output.mp3")
		);
	}
	/**
	 * adtsにコンバートする
	 * @throws Exception
	 */
//	@Test
	public void adts() throws Exception {
		logger.info("adtsに変換する動作テスト");
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("aac.flv")
			),
			new AdtsUnitWriter("output.aac")
		);
	}
	/**
	 * oggにコンバートする(speexのみ)
	 * @throws Exception
	 */
	@Test
	public void ogg() throws Exception {
		logger.info("oggに変換する動作テスト");
		HeaderFrame headerFrame = new HeaderFrame();
		headerFrame.fillWithFlvDefault();
		logger.info(HexUtil.toHex(headerFrame.getData(), true));
		CommentFrame commentFrame = new CommentFrame();
		logger.info(HexUtil.toHex(commentFrame.getData(), true));
		/*
		 * absoluteGranulePositionの設定が必要みたいだが、どういうことがよくわからん。
		 * よって解析する。
		 * mario.speex.oggで値を確認してみる。
		 * 0x27c13 + 27d94
		 * 0x4F9A7 + 27d7b
		 * 0x77722 + 27d73
		 * 0x9F495
		 * 
		 * speexのheaderFrameによると640samplesみたいなので、
		 * 255 x 640 = 0x27D80
		 * それっぽい値にはなってますね。なんで揺らぎがあるのかは不明
		 * どうやら経過sampleNumがはいっているのはガチっぽいです。ただし、なぜかフレームの保持sample数の半分が引かれているっぽいです。
		 * 仕様からすると引かなくても良さそうだけど・・・
		 */
//		convertTest(
//			FileReadChannel.openFileReadChannel(
//					Thread.currentThread().getContextClassLoader().getResource("speex.flv")
//			),
//			new AdtsUnitWriter("output.aac")
//		);
	}
	/**
	 * 内部処理
	 * @param source
	 * @param writer
	 */
	private void convertTest(IFileReadChannel source, IWriter writer) {
		// headerを書き込む
		try {
			writer.prepareHeader();
			IReader reader = new FlvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof VideoTag) {
					VideoTag vTag = (VideoTag)container;
					writer.addFrame(vTag.getFrame());
				}
				else if(container instanceof AudioTag) {
					AudioTag aTag = (AudioTag)container;
					writer.addFrame(aTag.getFrame());
				}
			}
			writer.prepareTailer();
		}
		catch(Exception e) {
			
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
		// tailerを書き込む
	}
}
