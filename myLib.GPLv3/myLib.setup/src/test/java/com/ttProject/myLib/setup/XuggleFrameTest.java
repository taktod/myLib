package com.ttProject.myLib.setup;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

/**
 * myLib.frame用のメディアデータセットアップ
 * @author taktod
 */
public class XuggleFrameTest extends SetupBase {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(XuggleFrameTest.class);
	/**
	 * flv1の検証用データ
	 * @throws Exception
	 */
//	@Test
	public void flv1() throws Exception {
		logger.info("flv1(flv)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "flv1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.flv1(container), null);
	}
	/**
	 * mp3用の検証用データ
	 * @throws Exception
	 */
//	@Test
	public void mp3() throws Exception {
		logger.info("mp3(flv)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "mp3.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.mp3(container));
		logger.info("mp3(mp3)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "mp3.mp3"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.mp3(container));
		logger.info("mp3(mp4)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "mp3.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.mp3(container));
		logger.info("mp3(mkv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "mp3.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.mp3(container));
	}
	/**
	 * h264用の検証用データ
	 * @throws Exception
	 */
//	@Test
	public void h264() throws Exception {
		logger.info("h264(flv)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "h264.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), null);
		logger.info("h264(mpegts)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "h264.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), null);
		logger.info("h264(mp4)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "h264.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), null);
		logger.info("h264(mkv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "h264.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), null);
	}
	/**
	 * aac用の検証用データ
	 * @throws Exception
	 */
//	@Test
	public void aac() throws Exception {
		logger.info("aac(flv)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("aac(adts)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("aac(mpegts)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("aac(mp4)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("aac(mkv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "aac.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
	}
	/**
	 * adpcmswf用の検証用データ
	 * @throws Exception
	 */
//	@Test
	public void adpcmswf() throws Exception {
		logger.info("adpcmswf(flv)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "adpcmswf.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.adpcm_swf(container));
	}
	/**
	 * nellymoser用の検証用データ
	 * @throws Exception
	 */
//	@Test
	public void nellymoser() throws Exception {
		logger.info("nellymoser(flv)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "nellymoser.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		IStreamCoder encoder = Encoder.nellymoser(container);
		encoder.setChannels(1);
		processConvert(container, null, encoder);
	}
	/**
	 * speex用の検証用データ
	 * @throws Exception
	 */
	@Test
	public void speex() throws Exception {
		logger.info("speex(flv)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "speex.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		IStreamCoder encoder = Encoder.speex(container);
		encoder.setSampleRate(16000);
		encoder.setChannels(1);
		processConvert(container, null, encoder);
		logger.info("speex(ogg)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.GPLv3/myLib.xuggle.test", "speex.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.speex(container));
	}
}
