package com.ttProject.myLib.setup;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Test;

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
	private int audioCounter = 0;
	private int videoCounter = 0;

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
		long startPos = 1024 * audioCounter / 44100 * 1000;
		for(int i = 0;i < samplesNum / 8;i ++, audioCounter ++) {
			short data = (short)(Math.sin(rad * audioCounter) * max);
			for(int j = 0;j < channels;j ++) {
				buffer.putShort(data);
			}
		}
		buffer.flip();
		int snum = (int)(buffer.remaining() * 8/bit/channels);
		IAudioSamples samples = IAudioSamples.make(snum, channels, Format.FMT_S16P);
		samples.getData().put(buffer.array(), 0, 0, buffer.remaining());
		samples.setComplete(true, snum, samplingRate, channels, Format.FMT_S16P, 0);
		samples.setTimeStamp(startPos);
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
		videoCounter ++;
		return picture;
	}
	/**
	 * ファイルを作成する
	 * @param path
	 * @param file
	 * @return
	 */
	public String getTargetFile(String file) {
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
	/**
	 * mp3のテスト用データを作成する。
	 * @throws Exception
	 */
	@Test
	public void mp3Setup() throws Exception {
		System.out.println("mp3のテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		// TODO ここの動作では、パスのディレクトリがないとだめ。
		if(container.open(getTargetFile("myLib.media.mp3/src/test/resources/test.mp3"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_MP3);
		IStreamCoder coder = stream.getStreamCoder();
		coder.setSampleRate(44100);
		coder.setChannels(2);
		coder.setBitRate(96000);
		if(coder.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		if(container.writeHeader() < 0) {
			throw new Exception("header書き込み失敗");
		}
		int counter = 0;
		while(counter < 400) {
			counter ++;
			IPacket packet = IPacket.make();
			IAudioSamples samples = samples();
			int samplesConsumed = 0;
			while(samplesConsumed < samples.getNumSamples()) {
				int retval = coder.encodeAudio(packet, samples, samplesConsumed);
				if(retval < 0) {
					throw new Exception("変換失敗");
				}
				samplesConsumed +=  retval;
				if(packet.isComplete()) {
					if(container.writePacket(packet) < 0) {
						throw new Exception("コンテナ書き込み失敗");
					}
				}
			}
		}
		if(container.writeTrailer() < 0) {
			throw new Exception("tailer書き込み失敗");
		}
		coder.close();
		container.close();
	}
	/**
	 * aacのテスト用データを作成する
	 * @throws Exception
	 */
	@Test
	public void aacSetup() throws Exception {
		System.out.println("aacのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.media.aac/src/test/resources/test.aac"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_AAC);
		IStreamCoder coder = stream.getStreamCoder();
		coder.setSampleRate(44100);
		coder.setChannels(2);
		coder.setBitRate(96000);
		if(coder.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		if(container.writeHeader() < 0) {
			throw new Exception("header書き込み失敗");
		}
		int counter = 0;
		while(counter < 400) {
			counter ++;
			IPacket packet = IPacket.make();
			IAudioSamples samples = samples();
			int samplesConsumed = 0;
			while(samplesConsumed < samples.getNumSamples()) {
				int retval = coder.encodeAudio(packet, samples, samplesConsumed);
				if(retval < 0) {
					throw new Exception("変換失敗");
				}
				samplesConsumed +=  retval;
				if(packet.isComplete()) {
					if(container.writePacket(packet) < 0) {
						throw new Exception("コンテナ書き込み失敗");
					}
				}
			}
		}
		if(container.writeTrailer() < 0) {
			throw new Exception("tailer書き込み失敗");
		}
		coder.close();
		container.close();
	}
	/**
	 * h264のテスト用データを作成する。
	 * @throws Exception
	 */
	@Test
	public void h264Setup() throws Exception {
		System.out.println("h264のテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.media.h264/src/test/resources/test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_H264);
		IStreamCoder coder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		coder.setNumPicturesInGroupOfPictures(5); // gopを30にしておく。keyframeが30枚ごとになる。
		
		coder.setBitRate(650000); // 250kbps
		coder.setBitRateTolerance(9000);
		coder.setPixelType(IPixelFormat.Type.YUV420P);
		coder.setWidth(320);
		coder.setHeight(240);
		coder.setGlobalQuality(10);
		coder.setFrameRate(frameRate);
		coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		coder.setProperty("level", "30");
		coder.setProperty("coder", "0");
		coder.setProperty("qmin", "10");
		coder.setProperty("bf", "0");
		coder.setProperty("wprefp", "0");
		coder.setProperty("cmp", "+chroma");
		coder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
		coder.setProperty("me_method", "hex");
		coder.setProperty("subq", "5");
		coder.setProperty("me_range", "16");
		coder.setProperty("keyint_min", "25");
		coder.setProperty("sc_threshold", "40");
		coder.setProperty("i_qfactor", "0.71");
		coder.setProperty("b_strategy", "0");
		coder.setProperty("qcomp", "0.6");
		coder.setProperty("qmax", "30");
		coder.setProperty("qdiff", "4");
		coder.setProperty("directpred", "0");
		coder.setProperty("cqp", "0");
		coder.setFlag(Flags.FLAG_LOOP_FILTER, true);
		coder.setFlag(Flags.FLAG_CLOSED_GOP, true);
		if(coder.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		if(container.writeHeader() < 0) {
			throw new Exception("header書き込み失敗");
		}
		int counter = 0;
		while(counter < 50) {
			counter ++;
			IPacket packet = IPacket.make();
			IVideoPicture picture = image();
			if(coder.encodeVideo(packet, picture, 0) < 0) {
				throw new Exception("変換失敗");
			}
			if(packet.isComplete()) {
				if(container.writePacket(packet) < 0) {
					throw new Exception("コンテナ書き込み失敗");
				}
			}
		}
		if(container.writeTrailer() < 0) {
			throw new Exception("tailer書き込み失敗");
		}
		coder.close();
		container.close();
	}
	/**
	 * flvのテスト用データを生成する
	 * @throws Exception
	 */
	@Test
	public void flvSetup() throws Exception {
		System.out.println("flvのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.media.flv/src/test/resources/test.flv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_FLV1);
		IStreamCoder coder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		coder.setNumPicturesInGroupOfPictures(5); // gopを30にしておく。keyframeが30枚ごとになる。
		
		coder.setBitRate(650000); // 250kbps
		coder.setBitRateTolerance(9000);
		coder.setPixelType(IPixelFormat.Type.YUV420P);
		coder.setWidth(320);
		coder.setHeight(240);
		coder.setGlobalQuality(10);
		coder.setFrameRate(frameRate);
		coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		if(coder.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		stream = container.addNewStream(ICodec.ID.CODEC_ID_MP3);
		IStreamCoder coder2 = stream.getStreamCoder();
		coder2.setSampleRate(44100);
		coder2.setChannels(2);
		coder2.setBitRate(96000);
		if(coder2.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		if(container.writeHeader() < 0) {
			throw new Exception("header書き込み失敗");
		}
		int counter = 0;
		while(counter < 50) {
			counter ++;
			IPacket packet = IPacket.make();
			IVideoPicture picture = image();
			if(coder.encodeVideo(packet, picture, 0) < 0) {
				throw new Exception("変換失敗");
			}
			if(packet.isComplete()) {
				if(container.writePacket(packet) < 0) {
					throw new Exception("コンテナ書き込み失敗");
				}
			}
			while(true) {
				IAudioSamples samples = samples();
				int samplesConsumed = 0;
				while(samplesConsumed < samples.getNumSamples()) {
					int retval = coder2.encodeAudio(packet, samples, samplesConsumed);
					if(retval < 0) {
						throw new Exception("変換失敗");
					}
					samplesConsumed +=  retval;
					if(packet.isComplete()) {
						if(container.writePacket(packet) < 0) {
							throw new Exception("コンテナ書き込み失敗");
						}
					}
				}
				//		long startPos = 1024 * audioCounter / 44100 * 1000;
				if(1024 * audioCounter / 44100 * 1000 > videoCounter * 25000) {
					break;
				}
			}
		}
		if(container.writeTrailer() < 0) {
			throw new Exception("tailer書き込み失敗");
		}
		coder2.close();
		coder.close();
		container.close();
	}
	/**
	 * mpegtsのテスト用データを生成する
	 * @throws Exception
	 */
	@Test
	public void mpegtsSetup() throws Exception {
		System.out.println("mpegtsのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.media.mpegts/src/test/resources/test.ts"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_H264);
		IStreamCoder coder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		coder.setNumPicturesInGroupOfPictures(5); // gopを30にしておく。keyframeが30枚ごとになる。
		
		coder.setBitRate(650000); // 250kbps
		coder.setBitRateTolerance(9000);
		coder.setPixelType(IPixelFormat.Type.YUV420P);
		coder.setWidth(320);
		coder.setHeight(240);
		coder.setGlobalQuality(10);
		coder.setFrameRate(frameRate);
		coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		coder.setProperty("level", "30");
		coder.setProperty("coder", "0");
		coder.setProperty("qmin", "10");
		coder.setProperty("bf", "0");
		coder.setProperty("wprefp", "0");
		coder.setProperty("cmp", "+chroma");
		coder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
		coder.setProperty("me_method", "hex");
		coder.setProperty("subq", "5");
		coder.setProperty("me_range", "16");
		coder.setProperty("keyint_min", "25");
		coder.setProperty("sc_threshold", "40");
		coder.setProperty("i_qfactor", "0.71");
		coder.setProperty("b_strategy", "0");
		coder.setProperty("qcomp", "0.6");
		coder.setProperty("qmax", "30");
		coder.setProperty("qdiff", "4");
		coder.setProperty("directpred", "0");
		coder.setProperty("cqp", "0");
		coder.setFlag(Flags.FLAG_LOOP_FILTER, true);
		coder.setFlag(Flags.FLAG_CLOSED_GOP, true);
		if(coder.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		stream = container.addNewStream(ICodec.ID.CODEC_ID_MP3);
		IStreamCoder coder2 = stream.getStreamCoder();
		coder2.setSampleRate(44100);
		coder2.setChannels(2);
		coder2.setBitRate(96000);
		if(coder2.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		if(container.writeHeader() < 0) {
			throw new Exception("header書き込み失敗");
		}
		int counter = 0;
		while(counter < 50) {
			counter ++;
			IPacket packet = IPacket.make();
			IVideoPicture picture = image();
			if(coder.encodeVideo(packet, picture, 0) < 0) {
				throw new Exception("変換失敗");
			}
			if(packet.isComplete()) {
				if(container.writePacket(packet) < 0) {
					throw new Exception("コンテナ書き込み失敗");
				}
			}
			while(true) {
				IAudioSamples samples = samples();
				int samplesConsumed = 0;
				while(samplesConsumed < samples.getNumSamples()) {
					int retval = coder2.encodeAudio(packet, samples, samplesConsumed);
					if(retval < 0) {
						throw new Exception("変換失敗");
					}
					samplesConsumed +=  retval;
					if(packet.isComplete()) {
						if(container.writePacket(packet) < 0) {
							throw new Exception("コンテナ書き込み失敗");
						}
					}
				}
				//		long startPos = 1024 * audioCounter / 44100 * 1000;
				if(1024 * audioCounter / 44100 * 1000 > videoCounter * 25000) {
					break;
				}
			}
		}
		if(container.writeTrailer() < 0) {
			throw new Exception("tailer書き込み失敗");
		}
		coder2.close();
		coder.close();
		container.close();
	}
	/**
	 * mp4のテスト用データを生成する。
	 * @throws Exception
	 */
	@Test
	public void mp4Setup() throws Exception {
		System.out.println("mp4のテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.media.mp4/src/test/resources/test.mp4"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_H264);
		IStreamCoder coder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		coder.setNumPicturesInGroupOfPictures(5); // gopを30にしておく。keyframeが30枚ごとになる。
		
		coder.setBitRate(650000); // 250kbps
		coder.setBitRateTolerance(9000);
		coder.setPixelType(IPixelFormat.Type.YUV420P);
		coder.setWidth(320);
		coder.setHeight(240);
		coder.setGlobalQuality(10);
		coder.setFrameRate(frameRate);
		coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		coder.setProperty("level", "30");
		coder.setProperty("coder", "0");
		coder.setProperty("qmin", "10");
		coder.setProperty("bf", "0");
		coder.setProperty("wprefp", "0");
		coder.setProperty("cmp", "+chroma");
		coder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
		coder.setProperty("me_method", "hex");
		coder.setProperty("subq", "5");
		coder.setProperty("me_range", "16");
		coder.setProperty("keyint_min", "25");
		coder.setProperty("sc_threshold", "40");
		coder.setProperty("i_qfactor", "0.71");
		coder.setProperty("b_strategy", "0");
		coder.setProperty("qcomp", "0.6");
		coder.setProperty("qmax", "30");
		coder.setProperty("qdiff", "4");
		coder.setProperty("directpred", "0");
		coder.setProperty("cqp", "0");
		coder.setFlag(Flags.FLAG_LOOP_FILTER, true);
		coder.setFlag(Flags.FLAG_CLOSED_GOP, true);
		if(coder.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		stream = container.addNewStream(ICodec.ID.CODEC_ID_MP3);
		IStreamCoder coder2 = stream.getStreamCoder();
		coder2.setSampleRate(44100);
		coder2.setChannels(2);
		coder2.setBitRate(96000);
		if(coder2.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		if(container.writeHeader() < 0) {
			throw new Exception("header書き込み失敗");
		}
		int counter = 0;
		while(counter < 50) {
			counter ++;
			IPacket packet = IPacket.make();
			IVideoPicture picture = image();
			if(coder.encodeVideo(packet, picture, 0) < 0) {
				throw new Exception("変換失敗");
			}
			if(packet.isComplete()) {
				if(container.writePacket(packet) < 0) {
					throw new Exception("コンテナ書き込み失敗");
				}
			}
			while(true) {
				IAudioSamples samples = samples();
				int samplesConsumed = 0;
				while(samplesConsumed < samples.getNumSamples()) {
					int retval = coder2.encodeAudio(packet, samples, samplesConsumed);
					if(retval < 0) {
						throw new Exception("変換失敗");
					}
					samplesConsumed +=  retval;
					if(packet.isComplete()) {
						if(container.writePacket(packet) < 0) {
							throw new Exception("コンテナ書き込み失敗");
						}
					}
				}
				//		long startPos = 1024 * audioCounter / 44100 * 1000;
				if(1024 * audioCounter / 44100 * 1000 > videoCounter * 25000) {
					break;
				}
			}
		}
		if(container.writeTrailer() < 0) {
			throw new Exception("tailer書き込み失敗");
		}
		coder2.close();
		coder.close();
		container.close();
	}
	/**
	 * mkvのテスト用データを生成する。
	 * @throws Exception
	 */
	@Test
	public void mkvSetup() throws Exception {
		System.out.println("mkvのテスト用データを作成する。");
		audioCounter = 0;
		videoCounter = 0;
		// flvデータを作ります。
		IContainer container = IContainer.make();
		if(container.open(getTargetFile("myLib.media.mkv/src/test/resources/test.mkv"), IContainer.Type.WRITE, null) < 0) {
			throw new Exception("開けませんでした");
		}
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_VP8);
		IStreamCoder coder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		coder.setNumPicturesInGroupOfPictures(5); // gopを30にしておく。keyframeが30枚ごとになる。
		
		coder.setBitRate(650000); // 250kbps
		coder.setBitRateTolerance(9000);
		coder.setPixelType(IPixelFormat.Type.YUV420P);
		coder.setWidth(320);
		coder.setHeight(240);
		coder.setGlobalQuality(10);
		coder.setFrameRate(frameRate);
		coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		if(coder.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		stream = container.addNewStream(ICodec.ID.CODEC_ID_MP3);
		IStreamCoder coder2 = stream.getStreamCoder();
		coder2.setSampleRate(44100);
		coder2.setChannels(2);
		coder2.setBitRate(96000);
		if(coder2.open(null, null) < 0) {
			throw new Exception("エンコーダーが開けませんでした");
		}
		if(container.writeHeader() < 0) {
			throw new Exception("header書き込み失敗");
		}
		int counter = 0;
		while(counter < 50) {
			counter ++;
			IPacket packet = IPacket.make();
			IVideoPicture picture = image();
			if(coder.encodeVideo(packet, picture, 0) < 0) {
				throw new Exception("変換失敗");
			}
			if(packet.isComplete()) {
				if(container.writePacket(packet) < 0) {
					throw new Exception("コンテナ書き込み失敗");
				}
			}
			while(true) {
				IAudioSamples samples = samples();
				int samplesConsumed = 0;
				while(samplesConsumed < samples.getNumSamples()) {
					int retval = coder2.encodeAudio(packet, samples, samplesConsumed);
					if(retval < 0) {
						throw new Exception("変換失敗");
					}
					samplesConsumed +=  retval;
					if(packet.isComplete()) {
						if(container.writePacket(packet) < 0) {
							throw new Exception("コンテナ書き込み失敗");
						}
					}
				}
				//		long startPos = 1024 * audioCounter / 44100 * 1000;
				if(1024 * audioCounter / 44100 * 1000 > videoCounter * 25000) {
					break;
				}
			}
		}
		if(container.writeTrailer() < 0) {
			throw new Exception("tailer書き込み失敗");
		}
		coder2.close();
		coder.close();
		container.close();
	}
}
