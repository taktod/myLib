/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.frame.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

import com.ttProject.util.HexUtil;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * データセットアップの基本的な動作
 * @author taktod
 */
public class SetupBase {
	/** 動作ロガー */
	private Logger logger = Logger.getLogger(SetupBase.class);
	/** audioデータの動作カウンター */
	private int audioCounter = 0;
	/** videoデータの動作カウンター */
	private int videoCounter = 0;
	/**
	 * 初期化
	 */
	protected void init() {
		audioCounter = 0;
		videoCounter = 0;
	}
	/**
	 * 変換の基幹部分を実行する動作
	 * @param container
	 * @param videoEncoder
	 * @param audioEncoder
	 */
	protected void processConvert(IContainer container, IStreamCoder videoEncoder, IStreamCoder audioEncoder) throws Exception {
		IVideoResampler videoResampler = null;
		IAudioResampler audioResampler = null;
		if(videoEncoder != null) {
			// 映像のpixelFormatを決定する。
			ICodec codec = videoEncoder.getCodec();
			// pixelFormatがYUV420Pに対応していなかったら別のを割り当てる。
			IPixelFormat.Type findType = null;
			for(IPixelFormat.Type type : codec.getSupportedVideoPixelFormats()) {
				if(findType == null) {
					findType = type;
				}
				if(type == IPixelFormat.Type.YUV420P) {
					findType = type;
					break;
				}
			}
			if(findType == null) {
				throw new Exception("対応している映像のPixelFormatが不明です。");
			}
			videoEncoder.setPixelType(findType);
			// coderを開く
			if(videoEncoder.open(null, null) < 0) {
				throw new Exception("映像エンコーダーが開けませんでした");
			}
		}
		if(audioEncoder != null) {
			// 音声のsampleFormatを決定する。
			ICodec codec = audioEncoder.getCodec();
			// AudioFormatはS16
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
				throw new Exception("対応している音声のSampleFormatが不明です。");
			}
			audioEncoder.setSampleFormat(findFormat);
			// coderを開く
			if(audioEncoder.open(null, null) < 0) {
				throw new Exception("音声エンコーダーが開けませんでした。");
			}
			// vorbisの場合はここにidentifierFrame commentFrame setupFrameが入るみたい。
			// setupframeは自力でつくるのがむずかしそうなので拾う必要がある。
//			IBuffer buffer = audioEncoder.getExtraData();
//			logger.info(HexUtil.toHex(buffer.getByteArray(0, buffer.getSize())));
//			return;
		}
		// containerのheaderを書く
		if(container.writeHeader() < 0) {
			throw new Exception("headerデータの書き込みが失敗しました。");
		}
		IPacket packet = IPacket.make();
		// packetの書き込み実行
		while(true) {
			IVideoPicture picture = null;
			if(videoEncoder != null) {
				picture = image();
				if(picture.getWidth() != videoEncoder.getWidth()
				|| picture.getHeight() != videoEncoder.getHeight()
				|| picture.getPixelType() != videoEncoder.getPixelType()) {
					if(videoResampler == null) {
						videoResampler = IVideoResampler.make(
								videoEncoder.getWidth(), videoEncoder.getHeight(), videoEncoder.getPixelType(),
								picture.getWidth(), picture.getHeight(), picture.getPixelType());
					}
					IVideoPicture pct = IVideoPicture.make(videoEncoder.getPixelType(), videoEncoder.getWidth(), videoEncoder.getHeight());
					int retVal = videoResampler.resample(pct, picture);
					if(retVal <= 0) {
						throw new Exception("映像リサンプル失敗");
					}
					picture = pct;
				}
				if(videoEncoder.encodeVideo(packet, picture, 0) < 0) {
					throw new Exception("映像変換失敗");
				}
				if(packet.isComplete()) {
					logger.info("映像Packet:" + picture.getWidth() + "x" + picture.getHeight());
					logger.info(packet.getSize());
					logger.info(HexUtil.toHex(packet.getByteBuffer()));
					if(container.writePacket(packet) < 0) {
						System.out.println(packet);
						packet.setDts(packet.getPts());
						throw new Exception("コンテナ書き込み失敗");
					}
				}
			}
			if(audioEncoder != null) {
				while(true) {
					IAudioSamples samples = samples();
					if(samples.getSampleRate() != audioEncoder.getSampleRate() 
					|| samples.getFormat() != audioEncoder.getSampleFormat()
					|| samples.getChannels() != audioEncoder.getChannels()) {
						if(audioResampler == null) {
							// resamplerを作る必要あり。
							audioResampler = IAudioResampler.make(
									audioEncoder.getChannels(), samples.getChannels(),
									audioEncoder.getSampleRate(), samples.getSampleRate(),
									audioEncoder.getSampleFormat(), samples.getFormat());
						}
						IAudioSamples spl = IAudioSamples.make(1024, audioEncoder.getChannels());
						int retVal = audioResampler.resample(spl, samples, samples.getNumSamples());
						if(retVal <= 0) {
							throw new Exception("音声サンプル失敗しました。");
						}
						samples = spl;
					}
					int samplesConsumed = 0;
					while(samplesConsumed < samples.getNumSamples()) {
						int retval = audioEncoder.encodeAudio(packet, samples, samplesConsumed);
						if(retval < 0) {
							throw new Exception("変換失敗");
						}
						samplesConsumed +=  retval;
						if(packet.isComplete()) {
							packet.setDts(packet.getPts());
							if(audioCounter > 10001) {
								logger.info("音声Packet:channel:" + samples.getChannels() + " sampleRate:" + samples.getSampleRate());
								logger.info(HexUtil.toHex(packet.getByteBuffer(), 0, packet.getSize(), false));
							}
//							System.out.println(HexUtil.toHex(packet.getByteBuffer()));
							if(container.writePacket(packet) < 0) {
								throw new Exception("コンテナ書き込み失敗");
							}
						}
					}
					if((picture != null && samples.getPts() > picture.getPts())
							|| samples.getPts() > 1000000) {
						break;
					}
				}
			}
			if(picture == null || picture.getPts() > 1000000) {
				break;
			}
		}
		// containerのtailer書き込み
		if(container.writeTrailer() < 0) {
			throw new Exception("tailerデータの書き込みが失敗しました。");
		}
		// おわり
		if(videoEncoder != null) {
			videoEncoder.close();
		}
		if(audioEncoder != null) {
			audioEncoder.close();
		}
		container.close();
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
			short data = 0;
			if(audioCounter < 10000) { // 10000 / 44100秒後に無音にしてみる。
				data = (short)(Math.sin(rad * audioCounter) * max);
			}
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
	/**
	 * 時間をベースにデータを応答してみる。
	 * @return
	 */
	private IVideoPicture image() {
		// とりあえずランダムな数値の表示されている画像をつくることにする。10fps
		BufferedImage base = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
		String message = Integer.toString((int)(Math.random() * 1000));
		Graphics g = base.getGraphics();
		g.setColor(Color.white);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
		g.drawString(message, 100, 100);
		g.dispose();
		IConverter converter = ConverterFactory.createConverter(base, IPixelFormat.Type.YUV420P);
		IVideoPicture picture = converter.toPicture(base, 25000 * videoCounter);
		// この時点ですでにtimestampは入力済みっぽいので、setPtsする必要はなさそう。
		picture.setPts(25000 * videoCounter);
		videoCounter ++;
		return picture;
	}
	/**
	 * ファイルを作成する
	 * @param path
	 * @param file
	 * @return
	 */
	protected String getTargetFile(String project, String file) {
		String target = "../" + project + "/src/test/resources/" + file;
		String[] data = target.split("/");
		File f = new File(".");
		f = new File(f.getAbsolutePath());
		f = f.getParentFile().getParentFile();
		for(String path : data) {
			f = new File(f.getAbsolutePath(), path);
		}
		f.getParentFile().mkdirs();
		return f.getAbsolutePath();
	}
}
