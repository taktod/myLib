/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU AFFERO GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.humble.test;

import java.nio.ByteBuffer;

import io.humble.ferry.Buffer;
import io.humble.video.AudioFormat.Type;
import io.humble.video.Codec;
import io.humble.video.AudioChannel.Layout;
import io.humble.video.Codec.ID;
import io.humble.video.Decoder;
import io.humble.video.Encoder;
import io.humble.video.MediaAudio;
import io.humble.video.MediaAudioResampler;
import io.humble.video.MediaPacket;
//import io.humble.video.MediaPictureResampler;

import io.humble.video.MediaPicture;
import io.humble.video.MediaPictureResampler;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.humble.frameutil.Packetizer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * flvの動作テスト
 * @author taktod
 */
public class FlvTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvTest.class);
	private Packetizer packetizer = null;
	// 音声
	private Decoder audioDecoder = null;
	private MediaAudioResampler audioResampler = null;
	private Encoder audioEncoder = null;
	// 映像
	private Decoder videoDecoder = null;
	private MediaPictureResampler videoResampler = null;
	private Encoder videoEncoder = null;
	/**
	 * 動作テスト
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		packetizer = new Packetizer();
		IReadChannel source = FileReadChannel.openFileReadChannel(
				"http://49.212.39.17/mario.flv"
		);
		// S_16 -> S_16Pに変換するとうまく動作しないっぽいな・・・なんだろう。
		// aacにエンコードしたい。
		audioEncoder = Encoder.make(Codec.findEncodingCodec(ID.CODEC_ID_MP3));
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setChannelLayout(Layout.CH_LAYOUT_STEREO);
		Type findFormat = null;
		for(Type format : audioEncoder.getCodec().getSupportedAudioFormats()) {
			if(findFormat == null) {
				findFormat = format;
			}
			if(format == Type.SAMPLE_FMT_FLT) {
				findFormat = format;
				break;
			}
		}
		if(findFormat == null) {
			throw new Exception("supported audioformat is unknown.");
		}
		audioEncoder.setSampleFormat(findFormat);
		audioEncoder.open(null, null);
		FlvTagReader reader = new FlvTagReader();
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			if(container instanceof AudioTag) {
				AudioTag aTag = (AudioTag)container;
				if(aTag.getFrame() != null) {
//					decodeAudio(aTag.getFrame());
				}
			}
			else if(container instanceof VideoTag) {
				VideoTag vTag = (VideoTag)container;
				if(vTag.getFrame() != null) {
					decodeVideo(vTag.getFrame());
				}
			}
		}
	}
	/**
	 * 映像をデコードします
	 * @param frame
	 */
	private void decodeVideo(IVideoFrame frame) throws Exception {
		logger.info("decode video");
		videoDecoder = packetizer.getDecoder(frame, videoDecoder);
		MediaPacket packet = packetizer.getPacket(frame, null);
		int offset = 0;
		while(offset < packet.getSize()) {
			logger.info(frame.getWidth());
			logger.info(frame.getHeight());
			MediaPicture picture = MediaPicture.make(frame.getWidth(), frame.getHeight(), io.humble.video.PixelFormat.Type.PIX_FMT_YUV420P);
			logger.info("here?");
			int bytesDecoded = videoDecoder.decodeVideo(picture, packet, offset);
			if(bytesDecoded < 0) {
				throw new Exception("failed to decode data.");
			}
			offset += bytesDecoded;
			if(picture.isComplete()) {
				logger.info("decode OK:" + picture);
			}
		}
	}
	/**
	 * 音声をデコードします
	 * @param frame
	 */
	private void decodeAudio(IAudioFrame frame) throws Exception {
		logger.info("decode audio");
		audioDecoder = packetizer.getDecoder(frame, audioDecoder);
		MediaPacket packet = packetizer.getPacket(frame, null);
		int offset = 0;
		while(offset < packet.getSize()) {
			MediaAudio samples = MediaAudio.make(frame.getSampleNum(), frame.getSampleRate(), frame.getChannel(), audioDecoder.getChannelLayout(), audioDecoder.getSampleFormat());
			int bytesDecoded = audioDecoder.decodeAudio(samples, packet, offset);
			if(bytesDecoded < 0) {
				throw new Exception("failed to decode data.");
			}
			offset += bytesDecoded;
			if(samples.isComplete()) {
				MediaAudio sampled = getResampled(samples);
				logger.info("encode now.");
				encodeSound(sampled);
			}
		}
		logger.info("end");
	}
	/**
	 * リサンプル実行
	 * @param samples
	 * @return
	 * @throws Exception
	 */
	private MediaAudio getResampled(MediaAudio samples) throws Exception {
		if(samples.getSampleRate() != audioEncoder.getSampleRate()
		|| samples.getFormat() != audioEncoder.getSampleFormat()
		|| samples.getChannels() != audioEncoder.getChannels()
		|| samples.getChannelLayout() != audioEncoder.getChannelLayout()) {
			// リサンプルする必要あり。
			if(audioResampler == null
			||    (samples.getSampleRate() != audioResampler.getInputSampleRate()
				|| samples.getFormat() != audioResampler.getInputFormat()
				|| samples.getChannels() != audioResampler.getInputChannels()
				|| samples.getChannelLayout() != audioResampler.getInputLayout())) {
					// resamplerがない場合もしくは、入力データがリサンプル動作に一致しない場合
				audioResampler = MediaAudioResampler.make(
						audioEncoder.getChannelLayout(), audioEncoder.getSampleRate(), audioEncoder.getSampleFormat(),
						samples.getChannelLayout(), samples.getSampleRate(), samples.getFormat());
				logger.info("リサンプルする必要があるので、リサンプラーを作成します。");
				logger.info(audioResampler.getInputLayout());
				logger.info(audioResampler.getInputFormat());
				logger.info(audioResampler.getInputSampleRate());
				logger.info(audioResampler.getInputChannels());
				logger.info(audioResampler.getOutputLayout());
				logger.info(audioResampler.getOutputFormat());
				logger.info(audioResampler.getOutputSampleRate());
				logger.info(audioResampler.getOutputChannels());
				audioResampler.open();
			}
			MediaAudio spl = MediaAudio.make(1024, audioEncoder.getSampleRate(), audioEncoder.getChannels(), audioEncoder.getChannelLayout(), audioEncoder.getSampleFormat());
			int retval = audioResampler.resample(spl, samples);
			if(retval <= 0) {
				throw new Exception("failed to resample audio.");
			}
			spl.setTimeStamp(samples.getTimeStamp());
			spl.setTimeBase(samples.getTimeBase());
			return spl;
		}
		else {
			// リサンプルする必要なし
			return samples;
		}
	}
	/**
	 * エンコード実行
	 * @param samples
	 * @throws Exception
	 */
	private void encodeSound(MediaAudio samples) throws Exception {
//		logger.info(samples.getNumSamples());
//		while(sampleConsumed < samples.getNumSamples()) {
			// 複数packetにしないとだめなデータの場合どうするんだろう・・・
		while(true) {
			MediaPacket output = MediaPacket.make();
			audioEncoder.encodeAudio(output, samples);
			logger.info(output);
			if(output.isComplete()) {
				Buffer buf = output.getData();
				ByteBuffer buffer = buf.getByteBuffer(0, buf.getBufferSize());
				logger.info(HexUtil.toHex(buffer, true));
				// ただしいかどうかはわからんけど、とりあえず、mp3にエンコードはできた。
			}
			else {
				break;
			}
//			logger.info(samples.getNumSamples());
//			break;
		}
	}
}
