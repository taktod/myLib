package com.ttProject.xuggle.flv.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.raw.AudioData;
import com.ttProject.util.DateUtil;
import com.ttProject.xuggle.flv.FlvDepacketizer;
import com.ttProject.xuggle.raw.AudioConverter;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IStreamCoder.Direction;
import com.xuggle.xuggler.IStreamCoder.Flags;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * データのエンコードを実行するテスト
 * @author taktod
 */
public class EncodeTest {
	private Logger logger = Logger.getLogger(EncodeTest.class);
	@Test
	public void h263Test() {
		FileChannel output = null;
		try {
			output = new FileOutputStream("h263.flv").getChannel();
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(true);
			flvHeader.setAudioFlg(false);
			output.write(flvHeader.getBuffer());

			IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_FLV1);
			IRational frameRate = IRational.make(15, 1); // 15fps(この値ではなく、入力値できまるっぽい。なんでだろう・・・)
			// デコード側にfps設定をいれても無駄っぽいです。都合のよいプログラムはなさそうですね。
			// 逆にいうとデータがきたらその分だけきちんと変換することで対応できるっぽいので、いいかもしれない
			encoder.setNumPicturesInGroupOfPictures(30); // gopを30にしておく。keyframeが30枚ごとになる。
			encoder.setBitRate(250000); // 250kbps
			encoder.setBitRateTolerance(9000);
			encoder.setPixelType(IPixelFormat.Type.YUV420P);
			encoder.setWidth(320);
			encoder.setHeight(240);
			encoder.setGlobalQuality(10);
			encoder.setFrameRate(frameRate);
			encoder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
			if(encoder.open(null, null) < 0) {
				throw new RuntimeException("エンコーダーが開けませんでした。");
			}
			// 画像データを変換可能なpixelデータに変換する。
			IConverter converter = null;
			converter = ConverterFactory.createConverter(new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR), IPixelFormat.Type.YUV420P);

			IPacket packet = IPacket.make();
			int index = 0;
			long startTime = -1;
			FlvDepacketizer depacketizer = new FlvDepacketizer();
			while(index < 1000) {
				index ++;
				long now = System.currentTimeMillis();
				if(startTime == -1) {
					startTime = now;
				}
				BufferedImage image = image();
				IVideoPicture picture = converter.toPicture(image, index * 166);
				picture.setPts(index * 66000);
				if(encoder.encodeVideo(packet, picture, 0) < 0) {
					throw new Exception("変換失敗");
				}
				if(packet.isComplete()) {
					for(Tag tag : depacketizer.getTag(encoder, packet)) {
						logger.info(tag);
						output.write(tag.getBuffer());
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("例外が発生しました。");
		}
		if(output != null) {
			try {
				output.close();
			}
			catch (Exception e) {
			}
			output = null;
		}
	}
	@Test
	public void avcTest() {
		FileChannel output = null;
		try {
			output = new FileOutputStream("avc.flv").getChannel();
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(true);
			flvHeader.setAudioFlg(false);
			output.write(flvHeader.getBuffer());

			IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_H264);
			IRational frameRate = IRational.make(15, 1); // 15fps
			encoder.setNumPicturesInGroupOfPictures(30); // gopを30にしておく。keyframeが30枚ごとになる。
			
			encoder.setBitRate(250000); // 250kbps
			encoder.setBitRateTolerance(9000);
			encoder.setPixelType(IPixelFormat.Type.YUV420P);
			encoder.setWidth(320);
			encoder.setHeight(240);
			encoder.setGlobalQuality(10);
			encoder.setFrameRate(frameRate);
			encoder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
			encoder.setProperty("level", "30");
			encoder.setProperty("coder", "0");
			encoder.setProperty("qmin", "10");
			encoder.setProperty("bf", "0");
			encoder.setProperty("wprefp", "0");
			encoder.setProperty("cmp", "+chroma");
			encoder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
			encoder.setProperty("me_method", "hex");
			encoder.setProperty("subq", "5");
			encoder.setProperty("me_range", "16");
			encoder.setProperty("keyint_min", "25");
			encoder.setProperty("sc_threshold", "0");
			encoder.setProperty("i_qfactor", "0.71");
			encoder.setProperty("b_strategy", "0");
			encoder.setProperty("qcomp", "0.6");
			encoder.setProperty("qmax", "30");
			encoder.setProperty("qdiff", "4");
			encoder.setProperty("directpred", "0");
			encoder.setProperty("profile", "main");
			encoder.setProperty("cqp", "0");
			encoder.setFlag(Flags.FLAG_LOOP_FILTER, true);
			encoder.setFlag(Flags.FLAG_CLOSED_GOP, true);
			if(encoder.open(null, null) < 0) {
				throw new RuntimeException("エンコーダーが開けませんでした。");
			}
			// 画像データを変換可能なpixelデータに変換する。
			IConverter converter = null;
			converter = ConverterFactory.createConverter(new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR), IPixelFormat.Type.YUV420P);

			IPacket packet = IPacket.make();
			int index = 0;
			long startTime = -1;
			FlvDepacketizer depacketizer = new FlvDepacketizer();
			while(index < 1000) {
				index ++;
				long now = System.currentTimeMillis();
				if(startTime == -1) {
					startTime = now;
				}
				BufferedImage image = image();
				IVideoPicture picture = converter.toPicture(image, index * 66000);
				picture.setPts(index * 66000);

				if(encoder.encodeVideo(packet, picture, 0) < 0) {
					throw new Exception("変換失敗");
				}
				if(packet.isComplete()) {
					for(Tag tag : depacketizer.getTag(encoder, packet)) {
						output.write(tag.getBuffer());
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("例外が発生しました。");
		}
		if(output != null) {
			try {
				output.close();
			}
			catch (Exception e) {
			}
			output = null;
		}
	}
	/**
	 * 表示画像をつくってみる。
	 * @return
	 */
	private BufferedImage image() {
		BufferedImage base = new BufferedImage(320, 240,
				BufferedImage.TYPE_3BYTE_BGR);
		String message = DateUtil.makeDateTime();
		Graphics g = base.getGraphics();
		g.setColor(Color.white);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
		g.drawString(message, 100, 100);
		g.dispose();
		return base;
	}
	private int audioCounter = 0; // audioの進捗カウンター
	@Test
	public void mp3Test() {
		audioCounter = 0;
		FileChannel output = null;
		AudioConverter converter = new AudioConverter();
		try {
			output = new FileOutputStream("mp3.flv").getChannel();
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(false);
			flvHeader.setAudioFlg(true);
			output.write(flvHeader.getBuffer());

			IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING,
					ICodec.ID.CODEC_ID_MP3);
			encoder.setSampleRate(44100);
			encoder.setChannels(2);
			encoder.setBitRate(96000);
			if (encoder.open(null, null) < 0) {
				throw new Exception("変換コーダーが開けませんでした。");
			}
			int index = 0;
			FlvDepacketizer depacketizer = new FlvDepacketizer();
			while (index < 2000) {
				index++;
				AudioData audioData = audioData();
				IAudioSamples samples = converter.makeSamples(audioData);
				IPacket packet = IPacket.make();
				int samplesConsumed = 0;
				while (samplesConsumed < samples.getNumSamples()) {
					int retval = encoder.encodeAudio(packet, samples,
							samplesConsumed);
					if (retval < 0) {
						throw new Exception("変換失敗");
					}
					samplesConsumed += retval;
					if (packet.isComplete()) {
						for (Tag tag : depacketizer.getTag(encoder, packet)) {
							output.write(tag.getBuffer());
						}
					}
				}
			}
			if (encoder != null) {
				encoder.close();
				encoder = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("例外が発生しました。");
		}
		if (output != null) {
			try {
				output.close();
			} catch (Exception e) {
			}
			output = null;
		}
	}
	@Test
	public void aacTest() {
		audioCounter = 0;
		FileChannel output = null;
		AudioConverter converter = new AudioConverter();
		try {
			output = new FileOutputStream("aac.flv").getChannel();
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(false);
			flvHeader.setAudioFlg(true);
			output.write(flvHeader.getBuffer());

			IStreamCoder encoder = IStreamCoder.make(Direction.ENCODING,
					ICodec.ID.CODEC_ID_AAC);
			encoder.setSampleRate(44100);
			encoder.setChannels(2);
			encoder.setBitRate(96000);
			if (encoder.open(null, null) < 0) {
				throw new Exception("変換コーダーが開けませんでした。");
			}
			int index = 0;
			FlvDepacketizer depacketizer = new FlvDepacketizer();
			while (index < 2000) {
				index++;
				AudioData audioData = audioData();
				IAudioSamples samples = converter.makeSamples(audioData);
				IPacket packet = IPacket.make();
				int samplesConsumed = 0;
				while (samplesConsumed < samples.getNumSamples()) {
					int retval = encoder.encodeAudio(packet, samples,
							samplesConsumed);
					if (retval < 0) {
						throw new Exception("変換失敗");
					}
					samplesConsumed += retval;
					if (packet.isComplete()) {
						for (Tag tag : depacketizer.getTag(encoder, packet)) {
							output.write(tag.getBuffer());
						}
					}
				}
			}
			if (encoder != null) {
				encoder.close();
				encoder = null;
			}
		} catch (Exception e) {
			Assert.fail("例外が発生しました。");
			e.printStackTrace();
		}
		if (output != null) {
			try {
				output.close();
			} catch (Exception e) {
			}
			output = null;
		}
	}

	/**
	 * ラの音のaudioデータをつくって応答する。
	 * 
	 * @return
	 */
	public AudioData audioData() {
		// とりあえずラの音で1024サンプル数つくることにする。
		int samplingRate = 44100;
		int tone = 440;
		int bit = 16;
		int channels = 2;
		int samplesNum = 1024;
		// 1024サンプル + 16bit + 2channels / (byte化)
		ByteBuffer buffer = ByteBuffer.allocate((int) samplesNum * bit
				* channels / 8);
		double rad = tone * 2 * Math.PI / samplingRate; // 各deltaごとの回転数
		double max = (1 << (bit - 2)) - 1; // 振幅の大きさ(音の大きさ)
		buffer.order(ByteOrder.LITTLE_ENDIAN); // xuggleで利用するデータはlittleEndianなのでlittleEndianを使うようにする。
		for (int i = 0; i < samplesNum / 8; i++, audioCounter++) {
			short data = (short) (Math.sin(rad * audioCounter) * max);
			for (int j = 0; j < channels; j++) {
				buffer.putShort(data);
			}
		}
		buffer.flip();
		return new AudioData(
				new AudioFormat(44100, bit, channels, true, false), buffer);
	}
}
