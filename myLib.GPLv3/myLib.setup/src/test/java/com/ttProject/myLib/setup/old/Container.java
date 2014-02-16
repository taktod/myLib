package com.ttProject.myLib.setup.old;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.myLib.setup.Encoder;
import com.ttProject.myLib.setup.SetupBase;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

/**
 * container用のテストデータ自動作成プログラム
 * continerは撤去して、containerTestにマージしておきたい。
 * @author taktod
 */
public class Container extends SetupBase {
	/** ロガー */
	private Logger logger = Logger.getLogger(Container.class);
	/**
	 * adts検証用データ
	 * @throws Exception
	 */
	@Test
	public void adts() throws Exception {
		logger.info("adts準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.adts", "test.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, null, Encoder.aac(container));
	}
	/**
	 * flvの検証用データ
	 * @throws Exception
	 */
	@Test
	public void flv() throws Exception {
		logger.info("flv準備 (flv1)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.flv", "test.flv"), IContainer.Type.WRITE, null) < 0) {
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
	/**
	 * mkvの検証用データ
	 * @throws Exception
	 */
	@Test
	public void mkv() throws Exception {
		logger.info("mkv準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mkv", "test.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.mp3(container));
	}
	/**
	 * mp3検証用データ
	 * @throws Exception
	 */
	@Test
	public void mp3() throws Exception {
		logger.info("mp3準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mp3", "test.mp3"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, null, Encoder.mp3(container));
	}
	/**
	 * mp4の検証用データ
	 * @throws Exception
	 */
	@Test
	public void mp4() throws Exception {
		logger.info("mp4準備(h264 / aac)");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mp4", "test.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
		// win8でうまく動作しなかった。
/*		logger.info("mp4準備(h264 / vorbis)");
		container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mp4", "test.h264vorbis.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.vorbis(container));*/
	}
	/**
	 * mpegtsの検証用データ
	 * @throws Exception
	 */
	@Test
	public void mpegts() throws Exception {
		logger.info("mpegts準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.mpegts", "test.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.h264(container), Encoder.aac(container));
	}
	/**
	 * oggの検証用データ
	 * @throws Exception
	 */
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
	/**
	 * webmの検証用データ
	 * @throws Exception
	 */
	@Test
	public void webm() throws Exception {
		logger.info("webm準備");
		init();
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.MIT/myLib.container.webm", "test.webm"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("コンテナが開けませんでした");
		}
		processConvert(container, Encoder.vp8(container), Encoder.vorbis(container));
	}
}
