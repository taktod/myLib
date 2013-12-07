package com.ttProject.myLib.setup;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.IStreamCoder.Flags;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * ここでセットアップを実行します。
 * 他のプロジェクト用のテストメディアデータも自動作成したいところ。
 * ただし、他のプロジェクトライブラリはほぼ使わない予定
 * @author taktod
 * 
 */
public class SetupForTest {
	private Logger logger = Logger.getLogger(SetupForTest.class);
	private int audioCounter = 0;
	private int videoCounter = 0;

	/**
	 * mp3のテスト用データを作成する。
	 * @throws Exception
	 */
	@Test
	public void mp3Setup() throws Exception {
		logger.info("mp3のテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.mp3/src/test/resources/test.mp3"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder audioEncoder = mp3(container);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
		container.close();
	}
	/**
	 * adpcmのflvを作成します
	 * @throws Exception
	 */
	@Test
	public void adpcmSwfSetup() throws Exception {
		logger.info("adpcmのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.adpcmswf/src/test/resources/test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder audioEncoder = adpcm_swf(container);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
		container.close();
	}
	/**
	 * nellymoserのflvを作成します
	 * @throws Exception
	 */
	@Test
	public void nellymoserSetup() throws Exception {
		logger.info("nellyMoserのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.nellymoser/src/test/resources/test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder audioEncoder = nellymoser(container);
		audioEncoder.setChannels(1);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
		container.close();
		logger.info("nellyMoser8のテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.nellymoser/src/test/resources/test_8.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		audioEncoder = nellymoser(container);
		audioEncoder.setSampleRate(8000);
		audioEncoder.setChannels(1);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
		container.close();
		logger.info("nellyMoser16のテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.nellymoser/src/test/resources/test_16.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		audioEncoder = nellymoser(container);
		audioEncoder.setSampleRate(16000);
		audioEncoder.setChannels(1);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
		container.close();
	}
	/**
	 * speexのflvを作成します
	 * @throws Exception
	 */
	@Test
	public void speexSetup() throws Exception {
		logger.info("speexのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.speex/src/test/resources/test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder audioEncoder = speex(container);
		audioEncoder.setChannels(1);
		audioEncoder.setSampleRate(16000);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
		container.close();
	}
	/**
	 * aacのテスト用データを作成する
	 * @throws Exception
	 */
	@Test
	public void aacSetup() throws Exception {
		logger.info("aacのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// aacデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.aac/src/test/resources/test.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder audioEncoder = aac(container);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
		container.close();
	}
	/**
	 * h264のテスト用データを作成する。
	 * @throws Exception
	 */
	@Test
	public void h264Setup() throws Exception {
		logger.info("h264のテスト用データを作成する。");
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.h264/src/test/resources/test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder videoEncoder = h264(container);
		processConvert(container, videoEncoder, null);
		videoEncoder.close();
		container.close();
	}
	/**
	 * flvのテスト用データを生成する
	 * @throws Exception
	 */
	@Test
	public void flvSetup() throws Exception {
		logger.info("flvのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.flv/src/test/resources/test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder videoEncoder = h264(container);
		IStreamCoder audioEncoder = aac(container);
		processConvert(container, videoEncoder, audioEncoder);
		audioEncoder.close();
		videoEncoder.close();
		container.close();
	}
	/**
	 * mpegtsのテスト用データを生成する
	 * @throws Exception
	 */
	@Test
	public void mpegtsSetup() throws Exception {
		logger.info("mpegtsのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.mpegts/src/test/resources/test.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder videoEncoder = h264(container);
		IStreamCoder audioEncoder = aac(container);
		processConvert(container, videoEncoder, audioEncoder);
		audioEncoder.close();
		videoEncoder.close();
		container.close();
	}
	/**
	 * mp4のテスト用データを生成する。
	 * @throws Exception
	 */
	@Test
	public void mp4Setup() throws Exception {
		logger.info("mp4のテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.mp4/src/test/resources/test.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder videoEncoder = h264(container);
		IStreamCoder audioEncoder = aac(container);
		processConvert(container, videoEncoder, audioEncoder);
		audioEncoder.close();
		videoEncoder.close();
		container.close();
	}
	/**
	 * mkvのテスト用データを生成する。
	 * @throws Exception
	 */
	@Test
	public void webmSetup() throws Exception {
		logger.info("webmのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.mkv/src/test/resources/test.webm"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder videoEncoder = vp8(container);
		IStreamCoder audioEncoder = vorbis(container);
		processConvert(container, videoEncoder, audioEncoder);
		audioEncoder.close();
		videoEncoder.close();
		container.close();
	}
	/**
	 * flv1のテスト用データを作成する。
	 * @throws Exception
	 */
	@Test
	public void flv1Setup() throws Exception {
		logger.info("flv1のテスト用データを作成する。");
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.media.flv1/src/test/resources/test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder videoEncoder = flv1(container);
		processConvert(container, videoEncoder, null);
		videoEncoder.close();
		container.close();
	}
	/**
	 * mp3Chunkのテスト用データを作成する。
	 * @throws Exception
	 */
	@Test
	public void mp3ChunkSetup() throws Exception {
		logger.info("mp3Chunkのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.chunk.mp3/src/test/resources/test.mp3"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder audioEncoder = mp3(container);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
		container.close();
	}
	/**
	 * aacChunkのテスト用データを作成する。
	 * @throws Exception
	 */
	@Test
	public void aacChunkSetup() throws Exception {
		logger.info("aacChunkのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// aacデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.chunk.aac/src/test/resources/test.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder audioEncoder = aac(container);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
		container.close();
	}
	/**
	 * mpegtsChunkのテスト用データを作成する。
	 * @throws Exception
	 */
	@Test
	public void mpegtsChunkSetup() throws Exception {
		logger.info("mpegtsChunkのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.chunk.mpegts/src/test/resources/test.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStreamCoder videoEncoder = h264(container);
		IStreamCoder audioEncoder = aac(container);
		processConvert(container, videoEncoder, audioEncoder);
		audioEncoder.close();
		videoEncoder.close();
		container.close();

		audioCounter = 0;
		videoCounter = 0;
		container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.chunk.mpegts/src/test/resources/test.noaudio.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		videoEncoder = h264(container);
//		audioEncoder = aac(container);
		processConvert(container, videoEncoder, null);
//		audioEncoder.close();
		videoEncoder.close();
		container.close();

		audioCounter = 0;
		videoCounter = 0;
		container = IContainer.make();
		if(container.open(getTargetFile("../myLib.MIT/myLib.chunk.mpegts/src/test/resources/test.novideo.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
//		videoEncoder = h264(container);
		audioEncoder = aac(container);
		processConvert(container, null, audioEncoder);
		audioEncoder.close();
//		videoEncoder.close();
		container.close();
	}
	// 以下エンコードの設定補助
	/**
	 * mp3
	 * @param container
	 * @return
	 */
	private IStreamCoder mp3(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_MP3);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * adpcm
	 * @param container
	 * @return
	 */
	private IStreamCoder adpcm_swf(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_ADPCM_SWF);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * nerrymoser
	 * @param container
	 * @return
	 */
	private IStreamCoder nellymoser(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_NELLYMOSER);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * nerrymoser
	 * @param container
	 * @return
	 */
	private IStreamCoder speex(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_SPEEX);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * aac
	 * @param container
	 * @return
	 */
	private IStreamCoder aac(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_AAC);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * vorbis
	 * @param container
	 * @return
	 */
	private IStreamCoder vorbis(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_VORBIS);
		IStreamCoder audioEncoder = stream.getStreamCoder();
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(2);
		audioEncoder.setBitRate(96000);
		return audioEncoder;
	}
	/**
	 * h264
	 * @param container
	 * @return
	 */
	private IStreamCoder h264(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_H264);
		IStreamCoder videoEncoder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		videoEncoder.setNumPicturesInGroupOfPictures(5); // gopを5にしておく。keyframeが5枚ごとになる。
		
		videoEncoder.setBitRate(650000); // 650kbps
		videoEncoder.setBitRateTolerance(9000);
		videoEncoder.setWidth(320);
		videoEncoder.setHeight(240);
		videoEncoder.setGlobalQuality(10);
		videoEncoder.setFrameRate(frameRate);
		videoEncoder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		videoEncoder.setProperty("level", "30");
		videoEncoder.setProperty("coder", "0");
		videoEncoder.setProperty("qmin", "10");
		videoEncoder.setProperty("bf", "0");
		videoEncoder.setProperty("wprefp", "0");
		videoEncoder.setProperty("cmp", "+chroma");
		videoEncoder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
		videoEncoder.setProperty("me_method", "hex");
		videoEncoder.setProperty("subq", "5");
		videoEncoder.setProperty("me_range", "16");
		videoEncoder.setProperty("keyint_min", "25");
		videoEncoder.setProperty("sc_threshold", "40");
		videoEncoder.setProperty("i_qfactor", "0.71");
		videoEncoder.setProperty("b_strategy", "0");
		videoEncoder.setProperty("qcomp", "0.6");
		videoEncoder.setProperty("qmax", "30");
		videoEncoder.setProperty("qdiff", "4");
		videoEncoder.setProperty("directpred", "0");
		videoEncoder.setProperty("profile", "main");
		videoEncoder.setProperty("cqp", "0");
		videoEncoder.setFlag(Flags.FLAG_LOOP_FILTER, true);
		videoEncoder.setFlag(Flags.FLAG_CLOSED_GOP, true);
		return videoEncoder;
	}
	/**
	 * vp8
	 * @param container
	 * @return
	 */
	private IStreamCoder vp8(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_VP8);
		IStreamCoder videoEncoder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		videoEncoder.setNumPicturesInGroupOfPictures(5); // gopを5にしておく。keyframeが5枚ごとになる。
		videoEncoder.setBitRate(650000); // 650kbps
		videoEncoder.setBitRateTolerance(9000);
		videoEncoder.setWidth(320);
		videoEncoder.setHeight(240);
		videoEncoder.setGlobalQuality(10);
		videoEncoder.setFrameRate(frameRate);
		videoEncoder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		return videoEncoder;
	}
	/**
	 * flv1
	 * @return
	 */
	private IStreamCoder flv1(IContainer container) {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_FLV1);
		IStreamCoder videoEncoder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		videoEncoder.setNumPicturesInGroupOfPictures(5); // gopを5にしておく。keyframeが5枚ごとになる。
		videoEncoder.setBitRate(650000); // 650kbps
		videoEncoder.setBitRateTolerance(9000);
		videoEncoder.setWidth(320);
		videoEncoder.setHeight(240);
		videoEncoder.setGlobalQuality(10);
		videoEncoder.setFrameRate(frameRate);
		videoEncoder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		return videoEncoder;
	}
	
	
	
	/**
	 * 変換の基幹部分を実行する動作
	 * @param container
	 * @param videoEncoder
	 * @param audioEncoder
	 */
	private void processConvert(IContainer container, IStreamCoder videoEncoder, IStreamCoder audioEncoder) throws Exception {
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
		// こっちはいらないっぽい。ただし別の関数っぽいので、やっとくに超したことはなさそうな・・・
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
	private String getTargetFile(String file) {
		String[] data = file.split("/");
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
