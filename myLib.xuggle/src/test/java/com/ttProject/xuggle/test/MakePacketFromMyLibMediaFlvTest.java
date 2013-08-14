package com.ttProject.xuggle.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.junit.Test;

import com.ttProject.media.aac.DecoderSpecificInfo;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.extra.flv.FlvOrderModel;
import com.ttProject.media.extra.mp4.IndexFileCreator;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.media.h264.ConfigData;
import com.ttProject.media.h264.DataNalAnalyzer;
import com.ttProject.media.h264.Frame;
import com.ttProject.media.h264.frame.PictureParameterSet;
import com.ttProject.media.h264.frame.SequenceParameterSet;
import com.ttProject.media.h264.frame.SliceIDR;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.atom.Moov;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.xuggle.test.swing.TestFrame;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.IStreamCoder.Direction;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * mediaデータをmyLib.media.flvのタグから再生成するようにしてみたい。
 * @author taktod
 */
public class MakePacketFromMyLibMediaFlvTest {
	/**
	 * 再生動作のテストをやってみる。
	 * とりあえず、h264のデータであることを前提にしてます。
	 * @throws Exception
	 */
//	@Test
	public void playTest() throws Exception {
		TestFrame frame = new TestFrame();
		// TODO このexit on closeはテスト動作では、有効にならないみたいです。
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			}
		});
		SequenceParameterSet sps = null;
		PictureParameterSet pps = null;
		// とりあえず、myLibのデータで動画データを読み込んでみる。
		IReadChannel fc = FileReadChannel.openFileReadChannel("http://49.212.39.17/rtype.mp4");
		IndexFileCreator analyzer = new IndexFileCreator(new File("mario.tmp"));
		Atom atom = null;
		while((atom = analyzer.analyze(fc)) != null) {
			if(atom instanceof Moov) {
				break;
			}
		}
		analyzer.updatePrevTag();
		analyzer.checkDataSize();
		analyzer.close();
		// 初期データの解析おわり。
		IReadChannel tmp = FileReadChannel.openFileReadChannel(new File("mario.tmp").getAbsolutePath());
		FlvOrderModel orderModel = new FlvOrderModel(tmp, true, false, 0); // 初めからデータを読み込んでいくことにする。
		// ここから先実データが取得可能になりますので、ここからコンバートかけてやれば問題なし。
		IStreamCoder coder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_H264);
		if(coder.open(null, null) < 0) {
			throw new Exception("");
		}
		// TODO 動作はするけど、h264のframeがないという判定になります。
		// たぶん、nal構造がくることを想定しているのでしょう。
		List<Tag> tagList;
		long firstTimestampInStream = Global.NO_PTS;
		long systemClockStartTime = 0;
		IPacket packet = IPacket.make();
		while((tagList = orderModel.nextTagList(fc)) != null) {
			for(Tag tag : tagList) {
				System.out.println(tag);
				// データタグ
				// ここでパケットをつくる。
				if(tag instanceof VideoTag) {
					VideoTag vTag = (VideoTag)tag;
					if(vTag.isMediaSequenceHeader()) {
						// mshの場合はspsとppsを含んでいるので、取得する必要あり。
						ConfigData configData = new ConfigData();
						// spsとppsを取得したい。
						IReadChannel rawData = new ByteReadChannel(vTag.getRawData());
						rawData.position(3);
						List<Frame> nals = configData.getNals(rawData);
						for(Frame nal : nals) {
							if(nal instanceof SequenceParameterSet) {
								sps = (SequenceParameterSet)nal;
							}
							else if(nal instanceof PictureParameterSet) {
								pps = (PictureParameterSet)nal;
							}
						}
						System.out.println(sps);
						System.out.println(pps);
//						System.out.println(HexUtil.toHex(sps.getData(), true));
//						System.out.println(HexUtil.toHex(pps.getData(), true));
//					throw new Exception("end");	
						continue;
					}
					ByteBuffer rawData = vTag.getRawData();
					rawData.position(7);
					int size = rawData.remaining();
					// このデータは複数のフレームを保持している可能性があるので、DataNalAnalyzerできちんと解析してフレームの部分だけ取り出しておきたい。
					IBuffer bufData = null;
					if(vTag.isKeyFrame()) {
						DataNalAnalyzer dataAnalyzer = new DataNalAnalyzer();
						rawData.position(3);
						IReadChannel rawDataChannel = new ByteReadChannel(rawData);
						Frame h264Frame = null;
						while((h264Frame = dataAnalyzer.analyze(rawDataChannel)) != null) {
							if(h264Frame instanceof SliceIDR) {
								// keyFrameの部分だけ抜き出したい。
								break;
							}
						}
						System.out.println("keyframe");
						packet.setKeyPacket(true);
						// keyFrameの場合はspsとppsも追加する必要あり。
						ByteBuffer spsData = sps.getData();
						ByteBuffer ppsData = pps.getData();
						ByteBuffer sliceIDRData = h264Frame.getData();
						ByteBuffer buffer = ByteBuffer.allocate(sliceIDRData.remaining() + 4 + spsData.remaining() + 4 + ppsData.remaining() + 4);
						buffer.putInt(1);
						buffer.put(spsData);
						buffer.putInt(1);
						buffer.put(ppsData);
						buffer.putInt(1);
						buffer.put(sliceIDRData);
						buffer.flip();
//						System.out.println(HexUtil.toHex(buffer.duplicate(), true));
						size = buffer.remaining();
						bufData = IBuffer.make(null, buffer.array(), 0, size);
					}
					else {
						System.out.println("innerframe");
						// nalにすればそのままでOK
						packet.setKeyPacket(false);
						ByteBuffer buffer = ByteBuffer.allocate(rawData.remaining() + 4);
						buffer.putInt(1);
						buffer.put(rawData.array(), 7, size);
						buffer.flip();
						size = buffer.remaining();
						bufData = IBuffer.make(null, buffer.array(), 0, buffer.remaining());
					}
					// bufDataはnalにしちゃおう。
					packet.setData(bufData);
					packet.setFlags(1);
					packet.setStreamIndex(0);
					packet.setDts(vTag.getTimestamp());
					packet.setPts(vTag.getTimestamp());
					packet.setTimeBase(IRational.make(1, 1000));
					// nal構造にもどしてやらないとだめなのか？
					packet.setComplete(true, size);
					// TODO はじめの状態でcodec情報とかが間違っていても問題なく動作するみたいです。
					IVideoPicture picture = IVideoPicture.make(coder.getPixelType(), coder.getWidth(), coder.getHeight());
					int offset = 0;
					while(offset < packet.getSize()) {
						System.out.println("フレームデコード開始");
						int bytesDecoded = coder.decodeVideo(picture, packet, offset);
						// このタイミングで勝手にcoderのサイズの変更もされるし、pictureのサイズもリサイズされるみたいです。
						if(bytesDecoded < 0) {
							throw new Exception("デコード中に問題が発生");
						}
						offset += bytesDecoded;
						if(picture.isComplete()) {
							System.out.println("pictureの読み込みおわり。");
							IVideoPicture newPic = picture;
							if(picture.getPixelType() != IPixelFormat.Type.BGR24) {
								IVideoResampler resampler = IVideoResampler.make(coder.getWidth(), coder.getHeight(), IPixelFormat.Type.BGR24, coder.getWidth(), coder.getHeight(), coder.getPixelType());
								newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
								if(resampler.resample(newPic, picture) < 0) {
									throw new Exception("リサンプル失敗");
								}
							}
							if(firstTimestampInStream == Global.NO_PTS) {
								firstTimestampInStream = picture.getTimeStamp();
								systemClockStartTime = System.currentTimeMillis();
							}
							else {
								long systemClockCurrentTime = System.currentTimeMillis();
								long millisecondsClockTimeSinceStartOfVideo = systemClockCurrentTime - systemClockStartTime;
								long millisecondsStreamTimeSinceStartOfVideo = (picture.getTimeStamp() - firstTimestampInStream) / 1000;
								final long milliSecondsTolerance = 50;
								final long milliSecondsToSleep = 
									millisecondsStreamTimeSinceStartOfVideo - (millisecondsClockTimeSinceStartOfVideo + milliSecondsTolerance);
								if(milliSecondsToSleep > 0) {
//									Thread.sleep(milliSecondsToSleep);
								}
							}
							IConverter converter = ConverterFactory.createConverter("XUGGLER-BGR-24", newPic);
							frame.getVideoComponent().setImage(converter.toImage(newPic));
						}
					}
				}
			}
		}
		if(coder != null) {
			coder.close();
			coder = null;
		}
	}
	/**
	 * こっちでは音声の動作テストをやってみる。
	 * とりあえずaacとmp3やっときたい。
	 * @throws Exception
	 */
//	@Test
	public void playTest2() throws Exception {
		SourceDataLine audioLine = null;
		// aacの場合はaacのヘッダー部のデータをつくる必要がありそうだ。
		// とりあえず、myLibのデータで動画データを読み込んでみる。
		IReadChannel fc = FileReadChannel.openFileReadChannel("http://49.212.39.17/rtype.mp4");
		IndexFileCreator analyzer = new IndexFileCreator(new File("mario.tmp"));
		Atom atom = null;
		while((atom = analyzer.analyze(fc)) != null) {
			if(atom instanceof Moov) {
				break;
			}
		}
		analyzer.updatePrevTag();
		analyzer.checkDataSize();
		analyzer.close();

		// 初期データの解析おわり。
		FileChannel outputTest = new FileOutputStream("mario.aac").getChannel();
		IReadChannel tmp = FileReadChannel.openFileReadChannel(new File("mario.tmp").getAbsolutePath());
		FlvOrderModel orderModel = new FlvOrderModel(tmp, false, true, 0); // 初めからデータを読み込んでいくことにする。
		// ここから先実データが取得可能になりますので、ここからコンバートかけてやれば問題なし。
		IStreamCoder coder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_AAC);
		System.out.println(coder.getCodecType());
		// TODO このあたりの情報をいれておかないと動作できないらしい。
		coder.setSampleRate(44100);
		coder.setTimeBase(IRational.make(1, 44100));
		coder.setChannels(2);
		if(coder.open(null, null) < 0) {
			throw new Exception("streamCoderを開くのに失敗しました。");
		}
		System.out.println(coder);
		AudioFormat audioFormat = new AudioFormat(coder.getSampleRate(),
				(int)IAudioSamples.findSampleBitDepth(coder.getSampleFormat()),
				coder.getChannels(), true /* 16bit samples */, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			audioLine = (SourceDataLine)AudioSystem.getLine(info);
			audioLine.open(audioFormat);
			audioLine.start();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("audioLineが開けませんでした。");
		}
		List<Tag> tagList;
		DecoderSpecificInfo dsi = null;
		IPacket packet = IPacket.make();
		int aacCount = 0;
		int pos = 0;
		while((tagList = orderModel.nextTagList(fc)) != null) {
			for(Tag tag : tagList) {
				if(tag instanceof AudioTag) {
					AudioTag aTag = (AudioTag)tag;
					if(aTag.isMediaSequenceHeader()) {
						dsi = new DecoderSpecificInfo();
						dsi.analyze(new ByteReadChannel(aTag.getRawData()));
						continue;
					}
					if(dsi == null) {
						throw new RuntimeException("decoderSpecificInfoが決定していません。");
					}
					ByteBuffer rawData = aTag.getRawData();
					int size = rawData.remaining();
					Aac aac = new Aac(size, dsi);
					aac.setData(rawData);
					ByteBuffer buffer = aac.getBuffer();
					outputTest.write(buffer.duplicate());
//					System.out.println(HexUtil.toHex(buffer.duplicate(), true));
					size = buffer.remaining();
					IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
					packet.setData(bufData);
					packet.setKeyPacket(true);
					packet.setFlags(1);
					packet.setStreamIndex(1);
					packet.setDts((int)(aacCount * 1024));
					packet.setPts((int)(aacCount * 1024));
					packet.setPosition(pos);
					pos += size;
					packet.setTimeBase(IRational.make(1, (int)(aac.getSamplingRate() * 1000)));
					aacCount ++;
					packet.setDuration(1024);
					packet.setComplete(true, size);
					System.out.println(packet);

					IAudioSamples samples = IAudioSamples.make(1024, coder.getChannels());
					int offset = 0;
					while(offset < packet.getSize()) {
						System.out.println("decodeのトライします。");
						int bytesDecoded = coder.decodeAudio(samples, packet, offset);
						if(bytesDecoded < 0) {
							throw new Exception("デコード中にエラーが発生");
						}
						offset += bytesDecoded;
						if(samples.isComplete()) {
							// 再生にまわす。
							System.out.println("completeできた。");
							byte[] rawBytes = samples.getData().getByteArray(0, samples.getSize());
							audioLine.write(rawBytes, 0, samples.getSize());
						}
					}
				}
			}
		}
		if(audioLine != null) {
			audioLine.drain();
			audioLine.close();
			audioLine = null;
		}
		if(coder != null) {
			coder.close();
			coder = null;
		}
		if(outputTest != null) {
			outputTest.close();
			outputTest = null;
		}
	}
	@Test
	public void playTest3() throws Exception {
		SourceDataLine audioLine = null;
		// aacの場合はaacのヘッダー部のデータをつくる必要がありそうだ。
		// とりあえず、myLibのデータで動画データを読み込んでみる。
		IReadChannel fc = FileReadChannel.openFileReadChannel("mario.mp3.mp4");
		IndexFileCreator analyzer = new IndexFileCreator(new File("mario.tmp"));
		Atom atom = null;
		while((atom = analyzer.analyze(fc)) != null) {
			if(atom instanceof Moov) {
				break;
			}
		}
		analyzer.updatePrevTag();
		analyzer.checkDataSize();
		analyzer.close();

		// 初期データの解析おわり。
		IReadChannel tmp = FileReadChannel.openFileReadChannel(new File("mario.tmp").getAbsolutePath());
		FlvOrderModel orderModel = new FlvOrderModel(tmp, false, true, 0); // 初めからデータを読み込んでいくことにする。
		// ここから先実データが取得可能になりますので、ここからコンバートかけてやれば問題なし。
		IStreamCoder coder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_MP3);
		System.out.println(coder.getCodecType());
		// TODO このあたりの情報をいれておかないと動作できないらしい。
		coder.setSampleRate(44100);
		coder.setTimeBase(IRational.make(1, 44100));
		coder.setChannels(2);
		if(coder.open(null, null) < 0) {
			throw new Exception("streamCoderを開くのに失敗しました。");
		}
		System.out.println(coder);
		AudioFormat audioFormat = new AudioFormat(coder.getSampleRate(),
				(int)IAudioSamples.findSampleBitDepth(coder.getSampleFormat()),
				coder.getChannels(), true /* 16bit samples */, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			audioLine = (SourceDataLine)AudioSystem.getLine(info);
			audioLine.open(audioFormat);
			audioLine.start();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("audioLineが開けませんでした。");
		}
		List<Tag> tagList;
		DecoderSpecificInfo dsi = null;
		IPacket packet = IPacket.make();
		int aacCount = 0;
		int pos = 0;
		while((tagList = orderModel.nextTagList(fc)) != null) {
			for(Tag tag : tagList) {
				if(tag instanceof AudioTag) {
					AudioTag aTag = (AudioTag)tag;
					if(aTag.isMediaSequenceHeader()) {
						dsi = new DecoderSpecificInfo();
						dsi.analyze(new ByteReadChannel(aTag.getRawData()));
						continue;
					}
					ByteBuffer rawData = aTag.getRawData();
					int size = rawData.remaining();
//					Aac aac = new Aac(size, dsi);
//					aac.setData(rawData);
//					ByteBuffer buffer = aac.getBuffer();
//					System.out.println(HexUtil.toHex(buffer.duplicate(), true));
//					size = buffer.remaining();
					IBuffer bufData = IBuffer.make(null, rawData.array(), 0, size);
					packet.setData(bufData);
					packet.setKeyPacket(true);
					packet.setFlags(1);
					packet.setStreamIndex(1);
					packet.setDts((int)(aacCount * 1024));
					packet.setPts((int)(aacCount * 1024));
					packet.setPosition(pos);
					pos += size;
					packet.setTimeBase(IRational.make(1, (int)(44100)));
					aacCount ++;
					packet.setDuration(1024);
					packet.setComplete(true, size);
					System.out.println(packet);

					IAudioSamples samples = IAudioSamples.make(1024, coder.getChannels());
					int offset = 0;
					while(offset < packet.getSize()) {
						System.out.println("decodeのトライします。");
						int bytesDecoded = coder.decodeAudio(samples, packet, offset);
						if(bytesDecoded < 0) {
							throw new Exception("デコード中にエラーが発生");
						}
						offset += bytesDecoded;
						if(samples.isComplete()) {
							// 再生にまわす。
							System.out.println("completeできた。");
							byte[] rawBytes = samples.getData().getByteArray(0, samples.getSize());
							audioLine.write(rawBytes, 0, samples.getSize());
						}
					}
				}
			}
		}
		if(audioLine != null) {
			audioLine.drain();
			audioLine.close();
			audioLine = null;
		}
		if(coder != null) {
			coder.close();
			coder = null;
		}
	}
}
