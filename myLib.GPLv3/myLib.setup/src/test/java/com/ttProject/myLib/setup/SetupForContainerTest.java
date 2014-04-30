package com.ttProject.myLib.setup;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

/**
 * container系の動作テスト用メディアデータ作成
 * @author taktod
 */
public class SetupForContainerTest extends SetupBase {
	/** ロガー */
	private Logger logger = Logger.getLogger(SetupForContainerTest.class);
	@Test
	public void adts() throws Exception {
		logger.info("adts準備 (aac)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.adts", "test.aac.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
	}
	@Test
	public void flv() throws Exception {
		logger.info("flv準備 (flv1)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.flv1(container), null);
		logger.info("flv準備 (flv1 / mp3)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1mp3.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.flv1(container), Encoder.mp3(container));
		// 一応存在するみたいですが、flvをつくることができないっぽいので、パスしておきます。
/*		logger.info("flv準備 (flv1 / mp38)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1mp38.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		IStreamCoder coder = Encoder.mp3(container);
		coder.setSampleRate(8000);
		processConvert(container, Encoder.flv1(container), coder);*/
		logger.info("flv準備 (flv1 / adpcmswf)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1adpcmswf.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.flv1(container), Encoder.adpcm_swf(container));
		logger.info("flv準備 (flv1 / nelly8)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1nelly8.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		IStreamCoder coder = Encoder.nellymoser(container);
		coder.setSampleRate(8000);
		coder.setChannels(1);
		processConvert(container, Encoder.flv1(container), coder);
		logger.info("flv準備 (flv1 / nelly16)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1nelly16.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		coder = Encoder.nellymoser(container);
		coder.setSampleRate(16000);
		coder.setChannels(1);
		processConvert(container, Encoder.flv1(container), coder);
		logger.info("flv準備 (flv1 / nelly)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv1nelly.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		coder = Encoder.nellymoser(container);
		coder.setChannels(1);
		processConvert(container, Encoder.flv1(container), coder);
		logger.info("flv準備 (h264 / mp3)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.h264mp3.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.mp3(container));
		logger.info("flv準備 (h264 / aac)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.h264aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("flv準備 (h264 / speex)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.h264speex.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		coder = Encoder.speex(container);
		coder.setSampleRate(16000);
		coder.setChannels(1);
		processConvert(container, Encoder.h264(container), coder);
		
		logger.info("flv準備 (adpcm44_2)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm44_2.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(44100);
		coder.setChannels(2);
		processConvert(container, null, coder);
		logger.info("flv準備 (adpcm44_1)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm44_1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(44100);
		coder.setChannels(1);
		processConvert(container, null, coder);
		logger.info("flv準備 (adpcm22_2)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm22_2.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(22050);
		coder.setChannels(2);
		processConvert(container, null, coder);
		logger.info("flv準備 (adpcm22_1)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm22_1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(22050);
		coder.setChannels(1);
		processConvert(container, null, coder);
		logger.info("flv準備 (adpcm11_2)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm11_2.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(11025);
		coder.setChannels(2);
		processConvert(container, null, coder);
		logger.info("flv準備 (adpcm11_1)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.adpcm11_1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		coder = Encoder.adpcm_swf(container);
		coder.setSampleRate(11025);
		coder.setChannels(1);
		processConvert(container, null, coder);
	}
	@Test
	public void mkv() throws Exception {
		logger.info("mkv準備 (h264 / mp3)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mkv", "test.h264mp3.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.mp3(container));
		logger.info("mkv準備 (h264 / aac)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mkv", "test.h264aac.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("mkv準備 (mjpeg/adpcm_ima_wav)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mkv", "test.mjpegadpcmimawav.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした。");
		}
		processConvert(container, Encoder.mjpeg(container), Encoder.adpcm_ima_wav(container));
	}
	@Test
	public void mp3() throws Exception {
		logger.info("mp3準備 (mp3)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mp3", "test.mp3.mp3"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, null, Encoder.mp3(container));
	}
	@Test
	public void mp4() throws Exception {
		logger.info("mp4準備 (h264 / aac)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mp4", "test.h264aac.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("mp4準備 (h264 / mp3)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mp4", "test.h264mp3.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.mp3(container));
	}
	@Test
	public void mpegts() throws Exception {
		logger.info("mpegts準備 (h264 / aac)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mpegts", "test.h264aac.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("mpegts準備 (h264 / mp3)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mpegts", "test.h264mp3.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
	}
	@Test
	public void ogg() throws Exception {
		logger.info("ogg準備 (vorbis)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.vorbis.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, null, Encoder.vorbis(container));
		logger.info("ogg準備 (speex)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.speex.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, null, Encoder.speex(container));
		logger.info("ogg準備 (theora / vorbis)");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.ogg", "test.theoravorbis.ogg"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.theora(container), Encoder.speex(container));
	}
	@Test
	public void webm() throws Exception {
		logger.info("webm準備 (vp8 / vorbis)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.webm", "test.webm"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.vp8(container), Encoder.vorbis(container));
	}
	@Test
	public void test() throws Exception {
		logger.info("test aac(adts)準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "aac.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("test aac(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
		logger.info("test mp3(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "mp3.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, null, Encoder.mp3(container));
		logger.info("test speex(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "speex.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		IStreamCoder encoder = Encoder.speex(container);
		encoder.setSampleRate(16000);
		encoder.setChannels(1);
		processConvert(container, null, encoder);
		logger.info("test h264(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "h264.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), null);
		logger.info("test h264/aac(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "h264_aac.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		logger.info("test flv1(flv)準備");
		init();
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.test", "flv1.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした");
		}
		processConvert(container, Encoder.flv1(container), null);
	}
}
