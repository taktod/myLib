package com.ttProject.xuggle.frameutil.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.util.HexUtil;
import com.ttProject.xuggle.frameutil.Depacketizer;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * 無音frameを作る動作テスト
 * @author taktod
 */
public class SilentFrameTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(SilentFrameTest.class);
	/**
	 * テスト動作
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("動作テスト開始");
		IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_VORBIS);
		encoder.setSampleRate(44100);
		encoder.setBitRate(96000);
		encoder.setChannels(2);
		ICodec codec = encoder.getCodec();
		Depacketizer depacketizer = new Depacketizer();
		IAudioSamples.Format findFormat = null;
		for(IAudioSamples.Format format : codec.getSupportedAudioSampleFormats()) {
			if(findFormat == null) {
				findFormat = format;
			}
			if(format == IAudioSamples.Format.FMT_S16) {
				findFormat = format;
				break;
			}
		}
		if(findFormat == null) {
			throw new Exception("対応しているAudioFormatが不明でした。");
		}
		encoder.setSampleFormat(findFormat);
		if(encoder.open(null, null) < 0) {
			throw new Exception("音声エンコーダーが開けませんでした");
		}
		IAudioSamples samples = IAudioSamples.make(44100, encoder.getChannels(), findFormat);
		samples.setComplete(true, 44100, encoder.getSampleRate(), encoder.getChannels(), findFormat, 0);
		samples.setTimeBase(IRational.make(1, encoder.getSampleRate()));
		int sampleConsumed = 0;
		int lastCount = 0;
		IPacket packet = IPacket.make();
		while(sampleConsumed < samples.getNumSamples()) {
			int retval = encoder.encodeAudio(packet, samples, sampleConsumed);
			if(retval < 0) {
				throw new Exception("変換失敗");
			}
			sampleConsumed += retval;
			if(packet.isComplete()) {
				IFrame frame = depacketizer.getFrame(encoder, packet);
				logger.info(frame.getCodecType());
				logger.info(HexUtil.toHex(frame.getData()));
				IAudioFrame aFrame = (IAudioFrame) frame;
				logger.info(aFrame.getSampleNum() + " : " + (sampleConsumed - lastCount));
				lastCount = sampleConsumed;
			}
		}
		logger.info("終わり");
	}
}
