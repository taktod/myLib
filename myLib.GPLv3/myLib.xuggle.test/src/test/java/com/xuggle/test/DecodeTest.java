/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.xuggle.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.FlvTagWriter;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.xuggle.frameutil.Depacketizer;
import com.ttProject.xuggle.frameutil.Packetizer;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec.ID;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * データのデコード処理を実施してみるテスト
 * @author taktod
 */
public class DecodeTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(DecodeTest.class);
	/** エンコード情報 */
	private int channels = 1;
	private int bitRate = 96000;
	private int sampleRate = 22050;
	private ICodec.ID codecId;

	/** エンコーダー */
	private IStreamCoder encoder = null;
	/** エンコード用処理パケット */
	private IPacket packet = IPacket.make();

	/** デコーダー */
	private IStreamCoder decoder = null;
	/** デコード用処理パケット */
	private IPacket decodedPacket = IPacket.make();

	/** frame -> packet変換 */
	private Packetizer packetizer = new Packetizer();
	/** packet -> frame変換 */
	private Depacketizer depacketizer = new Depacketizer();
	/** リサンプラー */
	private IAudioResampler resampler = null;
	private FlvTagWriter writer = null;
	/**
	 * 動作テスト
	 */
	@Test
	public void test() {
		try {
			IReadChannel source = FileReadChannel.openFileReadChannel(
					"xuggle_sound.error.flv"
			);
			FlvTagReader reader = new FlvTagReader();
			writer = new FlvTagWriter("output4.flv");
			writer.prepareHeader(CodecType.AAC);
			codecId = ID.CODEC_ID_MP3; // とりあえずmp3でいってみよう。
			openEncoder();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof VideoTag) {
//					logger.info(container);
				}
				else if(container instanceof AudioTag) {
//					logger.info(container);
					AudioTag aTag = (AudioTag) container;
					// そのまま入れ直しなら問題なし。
					IAudioFrame aFrame = aTag.getFrame();
					if(aFrame instanceof AudioMultiFrame) {
						AudioMultiFrame multiFrame = (AudioMultiFrame)aFrame;
						for(IAudioFrame af : multiFrame.getFrameList()) {
							decodeSound(af);
						}
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 音声データをデコードします。
	 * @param audioFrame
	 */
	private void decodeSound(IAudioFrame aFrame) throws Exception {
		if(aFrame instanceof AudioMultiFrame) {
			AudioMultiFrame multiFrame = (AudioMultiFrame)aFrame;
			for(IAudioFrame af : multiFrame.getFrameList()) {
				decodeSound(af);
			}
			return;
		}
		// このフレームをデコードして、何サンプル取得できたか確認しておきたいところ。
		decoder = packetizer.getDecoder(aFrame, decoder);
		if(decoder == null) {
			logger.warn("フレームのデコーダーが決定できませんでした。");
			return;
		}
		if(!decoder.isOpen()) {
			logger.info("デコーダーを開きます。");
			if(decoder.open(null, null) < 0) {
				throw new Exception("デコーダーが開けませんでした");
			}
		}
		IPacket pkt = packetizer.getPacket(aFrame, decodedPacket);
		if(pkt == null) {
			return;
		}
		decodedPacket = pkt;
		IAudioSamples samples = IAudioSamples.make(aFrame.getSampleNum(), decoder.getChannels());
		int offset = 0;
		while(offset < decodedPacket.getSize()) {
			int bytesDecoded = decoder.decodeAudio(samples, decodedPacket, offset);
			if(bytesDecoded < 0) {
				throw new Exception("データのデコードに失敗しました。");
			}
			offset += bytesDecoded;
			if(samples.isComplete()) {
				// ここで必要だったらリサンプル処理が必要
				samples = getResampled(samples);
				encodeSound(samples);
			}
		}
	}
	/**
	 * リサンプルをかけて、周波数を変換しておきます。
	 * @param samples
	 * @return
	 */
	private IAudioSamples getResampled(IAudioSamples samples) throws Exception {
		if(samples.getSampleRate() != encoder.getSampleRate()
		|| samples.getFormat()     != encoder.getSampleFormat()
		|| samples.getChannels()   != encoder.getChannels()) {
			if(resampler == null
			||    (samples.getSampleRate() != resampler.getInputRate()
				|| samples.getFormat()     != resampler.getInputFormat()
				|| samples.getChannels()   != resampler.getInputChannels())) {
				// リサンプラーがない、もしくは、リサンプラーの入力フォーマットと、現状の入力フォーマットが違う場合
				// リサンプラーを作り直す
				logger.info("resamplerを開きます。");
				logger.info(samples.getSampleRate());
				logger.info(samples.getFormat());
				logger.info(samples.getChannels());
				resampler = IAudioResampler.make(
						encoder.getChannels(), samples.getChannels(),
						encoder.getSampleRate(), samples.getSampleRate(),
						encoder.getSampleFormat(), samples.getFormat());
			}
			IAudioSamples spl = IAudioSamples.make(1024, encoder.getChannels());
			int retval = resampler.resample(spl, samples, samples.getNumSamples());
			if(retval <= 0) {
				throw new Exception("音声のリサンプルに失敗しました。");
			}
			spl.setPts(samples.getPts());
			spl.setTimeBase(samples.getTimeBase());
			return spl;
		}
		else {
			return samples;
		}
	}
	/**
	 * エンコード処理を実施します。
	 */
	private void encodeSound(IAudioSamples samples) throws Exception {
		// ここで必要だったらencoderを開く必要あり
		int sampleConsumed = 0;
		while(sampleConsumed < samples.getNumSamples()) {
			int retval = encoder.encodeAudio(packet, samples, sampleConsumed);
			if(retval < 0) {
				throw new Exception("変換失敗");
			}
			sampleConsumed += retval;
			if(packet.isComplete()) {
				IFrame frame = depacketizer.getFrame(encoder, packet);
				writer.addFrame(0x08, frame);
			}
		}
	}
	/**
	 * エンコーダーを開きます。
	 * @throws Exception
	 */
	private synchronized void openEncoder() throws Exception {
		if(encoder == null) {
			IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, codecId);
			coder.setChannels(channels);
			coder.setSampleRate(sampleRate);
			coder.setBitRate(bitRate);
			encoder = coder;
			// ここでencoderの作成から実施する必要あり
			ICodec codec = encoder.getCodec();
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
				throw new Exception("対応しているAudioFormatが不明です。");
			}
			encoder.setSampleFormat(findFormat);
			if(encoder.open(null, null) < 0) {
				throw new Exception("音声エンコーダーが開けませんでした");
			}
		}
	}
}
