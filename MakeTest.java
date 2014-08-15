/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.flv.FlvHeaderTag;
import com.ttProject.container.flv.FlvTagWriter;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.pcmalaw.PcmalawFrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * beep音からG711のflvを作りたい
 * @author taktod
 */
public class MakeTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MakeTest.class);
	/** audioデータの動作カウンター */
	private int audioCounter = 0;
	@Test
	public void test() {
		try {
			logger.info("開始");
			init();
			IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_PCM_ALAW);
			coder.setSampleRate(8000);
			coder.setChannels(1);
			processConvert(coder);
			logger.info("おわり");
		}
		catch(Exception e) {
			logger.error("例外発生", e);
		}
	}
	/**
	 * 初期化
	 */
	private void init() {
		audioCounter = 0;
	}
	/**
	 * 変換実施
	 */
	private void processConvert(IStreamCoder audioCoder) {
		IAudioResampler resampler = null;
		try {
			if(audioCoder != null) {
				ICodec codec = audioCoder.getCodec();
				IAudioSamples.Format findFormat = null;
				for(IAudioSamples.Format format : codec.getSupportedAudioSampleFormats()) {
					if(findFormat == null) {
						findFormat = format;
					}
					if(findFormat == IAudioSamples.Format.FMT_S16) {
						findFormat = format;
						break;
					}
				}
				if(findFormat == null) {
					throw new Exception("対応している音声フォーマットが不明です。");
				}
				audioCoder.setSampleFormat(findFormat);
				if(audioCoder.open(null, null) < 0) {
					throw new Exception("音声コーダーが開けませんでした。");
				}
			}
			IPacket packet = IPacket.make();
			PcmalawFrameAnalyzer analyzer = new PcmalawFrameAnalyzer();
			FlvTagWriter writer = new FlvTagWriter("output.flv");
			FlvHeaderTag headerTag = new FlvHeaderTag();
			headerTag.setAudioFlag(true);
			headerTag.setVideoFlag(false);
			writer.addContainer(headerTag);
			long pts = 0;
			while(true) {
				if(audioCoder != null) {
					IAudioSamples samples = samples();
					if(samples.getSampleRate() != audioCoder.getSampleRate() 
					|| samples.getFormat() != audioCoder.getSampleFormat()
					|| samples.getChannels() != audioCoder.getChannels()) {
						if(resampler == null) {
							// resamplerを作る必要あり。
							resampler = IAudioResampler.make(
									audioCoder.getChannels(), samples.getChannels(),
									audioCoder.getSampleRate(), samples.getSampleRate(),
									audioCoder.getSampleFormat(), samples.getFormat());
						}
						IAudioSamples spl = IAudioSamples.make(1024, audioCoder.getChannels());
						int retVal = resampler.resample(spl, samples, samples.getNumSamples());
						if(retVal <= 0) {
							throw new Exception("音声サンプル失敗しました。");
						}
						samples = spl;
					}
					int samplesConsumed = 0;
					while(samplesConsumed < samples.getNumSamples()) {
						int retval = audioCoder.encodeAudio(packet, samples, samplesConsumed);
						if(retval < 0) {
							throw new Exception("変換失敗");
						}
						samplesConsumed +=  retval;
						if(packet.isComplete()) {
							packet.setDts(packet.getPts());
							logger.info(packet.getPts());
							logger.info(1 / packet.getTimeBase().getDouble());
							IReadChannel readChannel = new ByteReadChannel(packet.getByteBuffer());
							IFrame frame = null;
							while((frame = analyzer.analyze(readChannel)) != null) {
								logger.info(frame);
								if(frame instanceof AudioFrame) {
									AudioFrame f = (AudioFrame)frame;
									f.setPts(pts);
									f.setTimebase(f.getSampleRate());
									pts += f.getSampleNum();
									writer.addFrame(8, frame);
								}
							}
							frame = analyzer.getRemainFrame();
							if(frame != null) {
								logger.info(frame);
								if(frame instanceof AudioFrame) {
									AudioFrame f = (AudioFrame)frame;
									f.setPts(pts);
									f.setTimebase(f.getSampleRate());
									pts += f.getSampleNum();
									writer.addFrame(8, frame);
								}
							}
						}
					}
					if(samples.getPts() > 1000000) {
						break;
					}
				}
				else {
					break;
				}
			}
			writer.prepareTailer();
		}
		catch(Exception e) {
			logger.error("例外発生", e);
		}
		finally {
			if(audioCoder.isOpen()) {
				audioCoder.close();
			}
		}
	}
	/**
	 * ラの音のaudioデータをつくって応答する。
	 * @return
	 */
	private IAudioSamples samples() {
		// とりあえずラの音で1024サンプル数つくることにする。
		int samplingRate = 44100;
		int tone = 440;
		int bit = 16;
		int channels = 2;
		int samplesNum = 1024;
		ByteBuffer buffer = ByteBuffer.allocate((int)samplesNum * bit * channels / 8);
		double rad = tone * 2 * Math.PI / samplingRate; // 各deltaごとの回転数
		double max = (1 << (bit - 2)) - 1; // 振幅の大きさ(音の大きさ)
		buffer.order(ByteOrder.LITTLE_ENDIAN); // xuggleで利用するデータはlittleEndianなのでlittleEndianを使うようにする。
		long startPos = 1000 * audioCounter / 44100 * 1000;
		for(int i = 0;i < samplesNum / 8;i ++, audioCounter ++) {
			short data = (short)(Math.sin(rad * audioCounter) * max);
			for(int j = 0;j < channels;j ++) {
				buffer.putShort(data);
			}
		}
		buffer.flip();
		int snum = (int)(buffer.remaining() * 8/bit/channels);
		IAudioSamples samples = IAudioSamples.make(snum, channels, Format.FMT_S16);
		samples.getData().put(buffer.array(), 0, 0, buffer.remaining());
		samples.setComplete(true, snum, samplingRate, channels, Format.FMT_S16, 0);
		// このtimestampの設定は必要っぽい
		samples.setTimeStamp(startPos);
		// こっちはいらないっぽい。ただし別の関数っぽいので、やっとくにこしたことはなさそうな・・・
		samples.setPts(startPos);
		return samples;
	}
}
