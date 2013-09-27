package com.ttProject.xuggle.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import com.ttProject.media.aac.DecoderSpecificInfo;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.extra.flv.FlvOrderModel;
import com.ttProject.media.extra.mp4.IndexFileCreator;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
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
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;
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
//				System.out.println(tag);
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
//					packet.setStreamIndex(0);
					packet.setDts(vTag.getTimestamp());
					packet.setPts(vTag.getTimestamp());
					packet.setTimeBase(IRational.make(1, 1000));
					// nal構造にもどしてやらないとだめなのか？
					packet.setComplete(true, size);
					// TODO はじめの状態でcodec情報とかが間違っていても問題なく動作するみたいです。
					IVideoPicture picture = IVideoPicture.make(coder.getPixelType(), coder.getWidth(), coder.getHeight());
					int offset = 0;
					while(offset < packet.getSize()) {
//						System.out.println("フレームデコード開始");
						int bytesDecoded = coder.decodeVideo(picture, packet, offset);
						// このタイミングで勝手にcoderのサイズの変更もされるし、pictureのサイズもリサイズされるみたいです。
						if(bytesDecoded < 0) {
							throw new Exception("デコード中に問題が発生");
						}
						offset += bytesDecoded;
						if(picture.isComplete()) {
							System.out.println(picture);
//							System.out.println("pictureの読み込みおわり。");
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
		System.out.println(info);
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
					size = buffer.remaining();
					IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
					packet.setData(bufData);
					packet.setComplete(true, size);

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
							System.out.println(samples);
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
//	@Test
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
					IBuffer bufData = IBuffer.make(null, rawData.array(), 0, size);
					packet.setData(bufData);
					packet.setComplete(true, size);

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
//	@Test
	public void test() {
		for(Mixer.Info info : AudioSystem.getMixerInfo()) {
			System.out.println(info);
		}
	}
	/**
	 * 音声動作にbeep音をmixしてみる。可能だったら別の音声をまじぇまじぇするプログラムもかきたいですね。
	 * @throws Exception
	 */
//	@Test
	public void playTest4() throws Exception {
		SourceDataLine audioLine = null;
		// ラの音(440hz)をまじぇまじぇしてみる。
		int tone = 440;
		double rad;
		double max = (1 << 14) - 1;
		// aacの場合はaacのヘッダー部のデータをつくる必要がありそうだ。
		// とりあえず、myLibのデータで動画データを読み込んでみる。
		IReadChannel fc = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.mp4");
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
		rad = tone * 2 * Math.PI / 44100;
		coder.setSampleRate(44100);
		coder.setTimeBase(IRational.make(1, 44100));
		coder.setChannels(2);
		if(coder.open(null, null) < 0) {
			throw new Exception("streamCoderを開くのに失敗しました。");
		}
		System.out.println(coder);
		System.out.println(IAudioSamples.findSampleBitDepth(coder.getSampleFormat()));
		AudioFormat audioFormat = new AudioFormat(coder.getSampleRate(),
				(int)IAudioSamples.findSampleBitDepth(coder.getSampleFormat()),
				coder.getChannels(), true /* 16bit samples */, true); // bigEndianをつかうように変更。
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		System.out.println(info);
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
		int i = 0;
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
					size = buffer.remaining();
					IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
					packet.setData(bufData);
					packet.setComplete(true, size);

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
							System.out.println(samples);
							// 再生にまわす。
							System.out.println("completeできた。");
							byte[] rawBytes = samples.getData().getByteArray(0, samples.getSize());
							ByteBuffer buf = ByteBuffer.allocate(rawBytes.length);
							buf.order(ByteOrder.LITTLE_ENDIAN); // リトルエンディアンでデータが入ることを想定
							buf.put(rawBytes);
							buf.flip();
							ByteBuffer result = ByteBuffer.allocate(buf.remaining());
							while(buf.remaining() > 0) {
								short data = (short)(Math.sin(rad * i) * max); // リトルエンディアンにしなきゃだめ。
								i ++;
								result.putShort((short)(buf.getShort() + data));
								result.putShort((short)(buf.getShort() + data));
							}
							result.flip();
							audioLine.write(result.array(), 0, samples.getSize());
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
	private boolean running = true;
//	@Test
	public void playTest5() throws Exception {
		TestFrame frame = new TestFrame();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				running = false;
			}
		});
		IFileReadChannel source = FileReadChannel.openFileReadChannel("test.flv");
		FlvHeader header = new FlvHeader();
		header.analyze(source);
		IStreamCoder coder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_VP6F);
		coder.setTimeBase(IRational.make(1, 1000));
//		coder.setFrameRate(IRational.make(359, 12));
//		coder.setWidth(320);
//		coder.setHeight(240);
		if(coder.open(null, null) < 0) {
			throw new Exception("デコーダーが開けませんでした");
		}
		System.out.println(coder);	
		IPacket packet = IPacket.make();
		long firstTimestampInStream = Global.NO_PTS;
		long systemClockStartTime = 0;
		IVideoResampler resampler = null;
		Tag tag = null;
		ITagAnalyzer analyzer = new TagAnalyzer();
		while((tag = analyzer.analyze(source)) != null) {
			if(tag instanceof VideoTag) {
				VideoTag vTag = (VideoTag) tag;
				ByteBuffer rawData = vTag.getRawData();
				int size = rawData.remaining();
				rawData.get();
				byte[] data = new byte[size];
				rawData.get(data);
				data[data.length - 1] = 0;
				IBuffer buf = IBuffer.make(null, data, 0, size);
				System.out.println(vTag.isKeyFrame());
				packet.setData(buf);
				packet.setFlags(0);
//				packet.setPosition(vTag.getPosition() + 12);
				packet.setDts(vTag.getTimestamp());
				packet.setPts(vTag.getTimestamp());
				packet.setTimeBase(IRational.make(1, 1000));
				packet.setComplete(true, size);
				packet.setKeyPacket(vTag.isKeyFrame());
				System.out.println(packet);
				System.out.println(HexUtil.toHex(packet.getData().getByteBuffer(0, size)));
				Thread.sleep(500);
				IVideoPicture picture = IVideoPicture.make(coder.getPixelType(), coder.getWidth(), coder.getHeight());
				int offset = 0;
				while(offset < packet.getSize()) {
					System.out.println("デコード");
					int bytesDecoded = coder.decodeVideo(picture, packet, offset);
					if(bytesDecoded < 0) {
						throw new Exception("デコード中に問題が発生しました。");
					}
					offset += bytesDecoded;
					if(picture.isComplete()) {
						IVideoPicture newPic = picture;
						if(coder.getPixelType() != IPixelFormat.Type.BGR24 && resampler == null) {
							// BGR24じゃなかったらpixelの書き換えが必須。
							resampler = IVideoResampler.make(coder.getWidth(), coder.getHeight(), IPixelFormat.Type.BGR24, coder.getWidth(), coder.getHeight(), coder.getPixelType());
							if(resampler == null) {
								throw new Exception("pixelリサンプラーが取得できませんでした。");
							}
						}
						if(resampler != null) {
							// リサンプルが必要な場合
							newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
							if(resampler.resample(newPic, picture) < 0) {
								throw new Exception("リサンプル失敗");
							}
						}
						if(newPic.getPixelType() != IPixelFormat.Type.BGR24) {
							throw new Exception("動画データがRGB24bitでない");
						}
						// この動画を表示させる時間を調べる。
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
								Thread.sleep(milliSecondsToSleep);
							}
						}
						IConverter converter = ConverterFactory.createConverter("XUGGLER-BGR-24", newPic);
						frame.getVideoComponent().setImage(converter.toImage(newPic));
					}
				}// */
			}
		}
		coder.close();
		source.close();
		while(running) {
			Thread.sleep(1000);
		}
		/*
com.xuggle.xuggler.IPacket@1335138976[complete:true;dts:25;pts:25;size:2831;key:false;flags:0;stream index:0;duration:0;position:-1;time base:1/1000;]
com.xuggle.xuggler.IPacket@1335138976[complete:true;dts:58;pts:58;size:46;key:false;flags:0;stream index:0;duration:0;position:-1;time base:1/1000;]
com.xuggle.xuggler.IPacket@1335138976[complete:true;dts:92;pts:92;size:1091;key:false;flags:0;stream index:0;duration:0;position:-1;time base:1/1000;]
com.xuggle.xuggler.IPacket@1335138976[complete:true;dts:125;pts:125;size:1507;key:false;flags:0;stream index:0;duration:0;position:-1;time base:1/1000;]
com.xuggle.xuggler.IPacket@1335138976[complete:true;dts:159;pts:159;size:2396;key:false;flags:0;stream index:0;duration:0;position:-1;time base:1/1000;]
com.xuggle.xuggler.IPacket@1335138976[complete:true;dts:192;pts:192;size:2605;key:false;flags:0;stream index:0;duration:0;position:-1;time base:1/1000;]
com.xuggle.xuggler.IPacket@1335138976[complete:true;dts:226;pts:226;size:2484;key:false;flags:0;stream index:0;duration:0;position:-1;time base:1/1000;]

true
com.xuggle.xuggler.IPacket@1355257904[complete:true;dts:25;pts:25;size:2831;key:true;flags:1;stream index:0;duration:0;position:764;time base:1/1000;]
00008402912602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFF20202660C02E0380F441001802006019018072060068180995601EAC1800E0500F47EAC18072060229482847D0BE5F1703011C2481CED93E07C779B8E2D0600AC1C07AC0800C02C8300CD01801BA0C00AD06003C4BF8062B00DF0FC180F28105528F7AAB915E8060401E0F55795D9F53446CB0EFFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFFF020E03C60410601B40301C07BE08610C18016A0C0BC00709001E104491F5C03C3F0600347C0800C0088301F1E1FCA23AAEAAB7CC9C7F070017FE0C03850601AC1805A0603AAFFE0C0370301120C0380301F1FFDDB8AC180152E57E9C1D6453F6A425400601541C005A0601AC1806106016C1806D060190210300E20C007784A0601C3C257BC250925C25F8BD578B8B8BA7D52B5D52B563E2E57F99EA07249F86CEC1803107001601807206007C180632F0601A41807104106003820972B04056019F0810B8BC7E087EAA84B1F0FA8FEABAAA5A3E2E2FF2AE403B1540354EFFFFF300600341400C00E50601901808AA5E10BD67FD01403F120B87F0BE0300BA0C039820095F85E01FA257BDE12BD152BAAFDF8A13BC9C180F400D00D121581F0601A15F87FF12C1801DF782117030110A87E5DE2FA0C00B8FFE0825C0C083A9FFFE0C07C0FC181FCFAB55FD1181A1020880C00784093F20420858AF67C18105F0EFDF56AD5FFDCC6FEAEFFA22FFF5BAF06060230481EFD58301102429578A7E0845DBFC694D51DCDB87FFFFF93C0601B81806FFD00CAA8037E2502183012010C20A854AA8301F1A1081801CF2B1F7D4CAA152B0603D47F7E3CCFAD5C53E0601C41806BAAC48FF81806F2F2E2EF028018107542404003CAD40301103F03BA3EAAC760A1C563C56A87AA25AC47028301143E060A184A06121150B0141808A1F03050C25030908A859FFFFC88480601A8208430600341806C2F540C03797894A8180EC060034210941044BF830020ACBC10021041124B950936D54AA89527B7F9E12E2B0863ED83DC06840893E0C0368300E34104218070940C0348FD5CA0C046890ABFEF02051E0F8BC7E0C00DC80823E9E1F5F7BE01FE0601C820C03B3FE0516977951D060602342028FF81808908522BFDFAB904A2EC57FADD563A1DDACEB66417C01825795D1FF848123C3FB1452FF0F95795DF46BD7F26D936E11FFFFD4501802808601A10C1800E57F041082AC2097A908547AAE2AF5904B06003C10003EAA039EF451EF893F939ABDE9C0103F17454D7A4490F00FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF980808080FFFFCC04040407FFFE602020203FFFF301010101FFFF
false
com.xuggle.xuggler.IPacket@1355257904[complete:true;dts:58;pts:58;size:46;key:false;flags:0;stream index:0;duration:0;position:4479;time base:1/1000;]
00008402B13FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFE17DEFFFFFFFFFFC0
false
com.xuggle.xuggler.IPacket@1355257904[complete:true;dts:92;pts:92;size:1091;key:false;flags:0;stream index:0;duration:0;position:4975;time base:1/1000;]
00008402B13FFFFFFFFFFFFFFFFFFFFFFFEB6240411FAB1204A565E5C252B082AFCD0945D83D5690FA98300480C0728300DCC0300520C0340309F40C00B42B0601BF509D0600541800F0601C0034034BFEA8218300320C039030023EF794C1F8079783001DF54AEEE0F3C0C045007892AD4B23BF4F608BA73FFFF6B0600441806C0601BD50300280C0068920C039AB2E1F8920C043F948210951497AB4CE6707001541C0FB00840C1518300BE0D245838009A01C0E03DB890104208420800C0500300C45C0C07C0923E1200FAB00EC54A810D5C543D57582E55AA55D6154AFFFFFDAC210068945E10C4856250F820896080AD5CE0410840840A11F765D540C07C970963E61401D2EFF293B38300500C0500300DC0C1690300C20C2418300320D34A8300520C0500300DC0C1690300C20C2418300320D34A3783001A0C0078401F806042F094258208430601C55CBFA0C07B8078282096AF6EC94180881F0943E1D08DEC2E9C6F69D0BEF785F7BC2FBDE17DEF0BEF785F7BC2FBDE17DEFFFF0BEF785F7BC2FBDE17DEF0BEF785F7BC2FBDE17DEFFFF0BEF785F7BC2FBDE17DEF0BEF785F7BC2FBDE17DEFFFAB84200F1F83010E257C7FA25450C84312C180881F01312C785E3C06120E936C3E9E0E0021FC180660601C2D0607B8180370260C03655740883011074180290600681807300F00DF897E12C0341801F086A156FA03003210C180F8572AE25AA1E2A0684081801706026018012A0C10783001E8C039552A0080400800801000394830110240902529CD0500FA1729C69A5B6B7A6D3C1803B00F0601B01806E1200344B0840C07703005FE03D80C0F8807FC20006F74181FA12307E3C02C0A3FA984C0E00208301D20C0370305D60C01E0309DDF069A14481F840084258303F6250F95A7FAAF5A86B81801DA0C01183003BFED0600482003090A1905F7BC2FBDE17DEF0BEF785F7BC2FBDE17DEF0BEF7FFD5C1800E0601BA89301007F42017C1284B110BEFA2B9E4BEBC7ECD369E0E00260301D20C0328305A80C01703091225A9400D07B0300540C00F0300E40C0398412F12550078300D60C0088300DD85D3FE80C0080062B0800843BC52AD59789625C2F1EB4AA49EC50AF74E0300280C04F0300360C14B0300220C2460900D3418060420600401800E0600354830116080100200F40D83010E252A1F29C6948F24DC30CE2417AA1FFF00F1781E4AA87A0C33F0640C00783010E0C0398305E83E06122E834DEC17DEF0BEF785F7BC2FBDE17DEF0BEF785F7BC2FBDFFF57084104208940803EF89210C7E5C240905DD565E5FFCF3A5F4FF3F362CF4F06008C180D3060088182E6060038184FD150300240C0078300D00C0090300DB55C06003C180721200368FB297C12AE8F27F546815CB891E1001808C00C060A2448061218B81A6822E54250943E0606107CA8B81838355EF0C217DEF0BEF785F7BC2FBDE17DEF0BEF785F7BC2FBDFFFF85F7BC2FBDE1637BDE17DEF0BEF785F7BFFFFE17DEF0BEF785F7BC2FBDFFFC0
false
com.xuggle.xuggler.IPacket@1355257904[complete:true;dts:125;pts:125;size:1507;key:false;flags:0;stream index:0;duration:0;position:6516;time base:1/1000;]
00008402B23FFFFFFFFFFFFFFFFFFFFFFFA983001C10C03CBC4AB2170070FC20E830509F1F79434D821AB54DA6D68E83002E0C07780783052C01E0C27D806034DF2C6A8BBF55AAF7EFAAAB601AADB83301010101FFFF601E1CC287FFFFB38300400C0378300CE5C0C008892084AC0301047FBE9F1E8943E52A926920300600C06A8900C1480300DE0D243B78300260C0090300DDE0601BBC3FFF84812448042FA80605E552852D88C83AB1C6BB87DCFFFFE9E0C0068921007E3FFE9700689601C0C1C2F84AFA94A0A12E2F680B636741801406023C034182E90410612141800D069A241801B0601C418067F03002025C1F2B121597EFA7F7DFF42C0CC1802906035C4A060A2C4B0613F07C2D47830022A81800D087EFCFFC1806D0412FC112C00E9E473C244D210600A4180D71281828B12C184FC1F0B403810003FC0C0398425410D57AFA2B63E6D50D05F03004C5E0C02D8F957D40301145F41847C06801130DF0722D70300580C06C0F81828B2F0613EC1A0040600B0180D81F0305165E0C27D834008E063FFFD4C1800D12C4B0601CFCAF0480405425F814FF00F05002A81400D03E0300240C07C8300D80C1490940C278034E14A60C0090301F20C036030524250309E00D3850200945E0C039FE8302F40803E12BCBC00D0432C06021D50C5107CB0F89025AB127D80A012950FDB523D1241418B603020A5FACAC781800F0508300E20C1460420613C81A70A4C3FC3CF2DE40E2E0421F0299501070300280860C03503051A0800C279AB069A28180150430601B41828B00C0613CA834D15FFFE9E0C0078960C038AA084AD42B2E125583070C2417896840F7AE1083002A0C0748900C1440300DE0C248041069A0C18011124207D5AB1EF8208922403070C07BA940F970340F80C00B830116258305DC0C03801100E069A293C180150603A448060A201806F06124020834D060C00A8301D2240305100C0378309201041A68306005C1808B12C182EE0601C008807034D140C00C030116258305DA0C0382100E069A2938303002A0C0750900C1440300DE0C248041069A0C180150603A448060A201806F06124020834D060C00C8301223F060BAC180700614281A689060060180921F8305D40C038030A140D344A783002A0C0750900C1440300DE0C248041069A0C180140603A848060A201806F06124003C1A6830600601809212C182EB0601C01850A069A24180170602484B060BAC180700614281A689FFFF0F1700D086104B846F83001CA8183F90863C069808A4180F30601B8182FA06006B0183F557A0D2C18D80684212C7FFD810018010569C21C3E520C046043060A3C1801E4B3D4FFFFF878AAB561068420431287E01D2B20A1A5E079BC0607EF7F901A4032C0603D81806D060BE01801C0613D95834D061C07A83010E0840845CD35A3A40A1A212506023C49060A381801DA94BBE7FFFFC3CB4180F50601B0182F706006C184F6F834D063C0C006892082AFFF0603E0BC1800E5738A01808B2FF97AE06F64C4CD992606023849060A3C1801E061210331A00C120BC7C3D521081800E129AC060233D7D3E2388E937843FFFE8C1FE5C194FF3A0C00B2800D0431FD540800C0087D4D523DFD03BFD033479DC340C00A60300E75B06003818011C641A8830D40A350A1A200102700FFFFFFFF4F06008C1806B0601B41808E1F8303EC108206C0609E8481247E0C2C18B41803B06036E0305CE0C00903091A01E0D37C8902484012020FAAA1247CA8BE50330F83001E0C04680703051401C0C2438649E01801C5E0A1120B8BE2B1EC57354CF28D025B706A0C00A896250300E355E89024842545CC7A842DA841A0FD1F7A4F4924C91A7005260AC1801B06021C1806B060BD80381848A069DF44A2EF5C1D88E480151C9A40C0087C1806C1F0FBFF12A2A12A03010F40F58A820333A0C17CF97D2106005558300D2A9451D8945DC11FE019F1D27E04131C8B41A0EBC063C041C1981008080FFFFFFFF4F0600347E0C037820090AE970FC492E2E571856AFCA8185FEB3480180130603A818011060BD4210309F23E069BFBC3CF6DFA89B532C29F288063C346F2E967E4FF9A7B82773FFFFFFFFFFFFFFFC
false
com.xuggle.xuggler.IPacket@1355257904[complete:true;dts:159;pts:159;size:2396;key:false;flags:0;stream index:0;duration:0;position:8907;time base:1/1000;]
00008402B33FFFFFFFFFFFFFFFFFFFFFFD4FDFB8E06003C180F3560C1468410614101A6FD6300E1F041F97FF284212CB93AB518595F502E811C6082F01520D24130174152F073BC4F0896C4A2F1FFD534252B2F060E0EAA0619FBE1928A5DE57B3BE2F549AF870013EBF02B53287A8007AAF2BD9D85EA935F0E0FFAFE608B53287FFFB3AA2FAA157FF96E4B72374881802C06038C208305185AAC58DE0C016F818063083A0C0C6B30182FC16590C84A1F97E08D4B91BC1800D03FF060C1408805B6D094252BAAD4EA8E635ACDFAF2133406A0C00901F0601C9600C0613F41A07C4DA06004C180F85606421034AFA0C0098301F2AC0C8420695F5CE4C00A8180140603E158190840D2BE8300280C07C2BD914842ED9014A65CEFFF4F9F9B0F03001A0C0450920C17884306120C21034D0DE57F1E7AFD687C1801B060394B8182872F0220149E0C006830112248305E210C184830840D3430F87C5DF2E9944B2F1F8307F8A34155E70300360C0729703050E5E044028180161281806A54258FFC3E0425707AD5DF5182D807007807F803BD3FF97FBF5193346698207B89C08BC27683E069BFA71396034DFCBB8FBCAAC577E07FFF94149542889E734B4F646EAD85AF4A17F2BF5FFAFA7EAAB445F6298914CB064A957AFA65FD2F02FF6EA0C8324E05A1F7014D26E040060224BC182F40820C283034D0C0C00D0301D2A9483030BF11C0CAA16FFFA2C18013050FC1829012C18506069BF65DCE75E0C0068F418072060A20480613E7E0D342078F8287E0C1480960C283034DFA3B021AB80C1078920C284034DF6300860C038830510108184F8F834D0A320860C038830512108184F8F834D0BFFFE880A4200FC7FF80C1040FCBC0A0F01867E0CCB8BA97D80A3551555A2B1E96A80CA01380AC17A6A0C0090285583051C24830A100D37E0300240A1560C1470920C284034DF85D957F0D0BBABF8688C22108485755EE84256246601B1E0ED20F3FF69393E2756931C5EAA1750607F15FD52CA0B87A0C33E8649008D52BB9ED9EAAC0B7B34B4F0300200C07C9783051E24830A100D37DF80E81619B602A1087C3F565E238905E250307FA3CEA41E7FE3349B8180100508FC1828E0860C284834DF60C0070218208305123E0613E2834D0BFFE9FEFDC8F060074180F200D03151020007D77BF14CAE06005410A0305D8AC184FC0840D3418797F81807300FE8EAF25BD04000F7198301E401E0A6560C2470340EC3AA87DDD0603C0BC7F72CC1E842AD834808440850182EB1F8309F61001A684FFFF0F281F0300E624D1E8F67E62AB603021801F3A82856620C0790068299583091C0D03B128214060BAC7E0C27D840069A109410A0305D63F0613EC20034D09FFFE1E5F01807200F0315183001A0D03B198301E401E0A6F83091A0D03B0F41F41E2B0605C07FF680EA9085697360C182714A7262204280C1758FC184FC0800D3427FFF86A340C006892AC7E3D1EEF14668422F1217D0607F2D5289A64D808B0422ECFA980823F12798A3F47D7DD640E2A9779C3A03FFFE9E2584300C12D50FBFFF178075A0C1FD2A2F06140FDFD783002160208306140C2427C5A01A3F00F557D4181090805C05E0219497BD50301F225019120184FD069DF5300F8300240C0B98306140C2427C5A0C00968300E006420030908AC1A6FE1287A250305F6A8184FC0C84A1E8900C17DAA0613F0320F300603C41056F83091E0D03B180301E201AB50612401A077220502A060BB04B0613F00381A686220502A060BB04B0613F00381A686FFFFFFF4FF2BD93988E9E0600895E6E41F03003204FE380C8B8BF7393D40A9A0600AE83002E10D700E027156834B0E1E4A0180C0FE755030023E80C184DD57AC088E250865C3C2FAA07C0C009E5060BE4745E8C0E3CB81800D0600384B0842581FFD12A8EAC0510F25E8142EF717DD44648C1807100C2F0823CBE042F5F6E81FCFEEE2FE60F7FFF879ADF5A0C1020300320441A704381E5060A10180190220D38221D06005421AE01C8A781A68341540C00B0439754842A9B3C0D3427FFF878C0F2400CCAA803B7046B0782421EE44E74D360305080C00D011D0698084C218FBEAC7C07954554183FD3A820C04B0920A70808E2AD06960FFFFC351D06003B00EE406003A340C0C6230362E0125060048100BC207D5D2F03C3BCC1E4F46B40C4F2E6407FFFD3005893F82551EAAF9717DA99459C5FC70B818089060038182891F030908E0508EFCAE01F5714B3E11D174D8300220C045AB060BDC03418503069A0D306CF00703001E5F28302103ED80C18278A4F8FC48060044103FBA019FF83070C01CABE5A73C01C2551287AA4491247E3D500C0FD8F3983D031E8064640C0378F8BD52A540C0FB8069775A1F73DD0691F926100180890840C1448F81848500A060078450340C00C08E0D50183004C0C0490923DBD1F08E0A62E069A17FFD262509354177EB4868C01803C06038403C182FC00C80C2048300E639704006022848DBF0500FB7EDEAA8F0D908180140601A0BC10403C18087FAB12AEFAFE4543E127D4749D5CA70304B03E07BE08594BC44D341EA8210945D54AA1287CAAAEA9547027FFF8686E07C180670530940C2440960D37F1302050601BFE5E0C0BC02178BA46BC3E57EBFA97FF300809FFFE1E340C037043F80700617892AAC955677FE56AE74150ABAF5DA0C02E841A0C13C8420612301A0750756ABCAFD7CAE6FAB71E200C045840060A2448061218B859FFFE1A9A8300360C0370FC180725624F87EAC4AF81EF2AB2170F84B2EA3BBF96D0436DA6084042150F87C5C0C0C2172A54053D3C3001FFFF4C2D89008210E0070401F8976A81E4DD515582ABC0D0BF0300580C06E170305F501848B0681DD4FA2B97DF9B2980800C04700703050CA8689410B060040180180601AC480601C7E24FBE5FB4B8BA5F4543E1F9737B36821B7E068E08BF4EF950F87C5C06952AF014F4724E7C0D5486418037A0C03004303E3C03E01D0146D0301140C0399C128180890840C1430F8184830CBFFD27BC9A4C0C00C0301EE0C1B580723545D01A67CF28918725C180101287E3F565F947E257C0A3658F74C4CE8701810FE3C1F7FE08704A060DF3F367880A4180F9A0C1750900C2422A069BEC081478A814DE020EFFFF0D45C100BC7A3D80C07A97041680D2AF2A1E36DA8B7548B804127BD1A9224700FFFFA401484A126A82EB40FC1F48D97D300C00D02868305D2108184852E069BFBC0795029BC81C1A8B806178F6AB94104B803E8EA81C2E85C3C6D6B754F121E02049EF46A4891C03FFFEAE0C00B8301F025B001E01C5F17D77878A80C7903BFFFFFFFFFD6FA9A27CA362343E8C5FFFFF80
false
com.xuggle.xuggler.IPacket@1355257904[complete:true;dts:192;pts:192;size:2605;key:false;flags:0;stream index:0;duration:0;position:11753;time base:1/1000;]
00008402B4BFFFFFFFFFFFFFFFFFFFFFEB63E2F2FAA9A2E565E0C1C1E710BD4C038180F8F83051224830A060D37EAA55F533547D526FB632AC17D0B18098515014C9FD5E2582C9B8BA272802FE9FD63FE4B0593700CA27064010114F0977FF901403F5405FEA30B3EF02FFFF0CC04040407FFFD81EFF25C533E45FF57060050180701FAB2E03CA07BD5206AE2CDF1E3E2FAAE01A553EBEAB5050F0F5E0603D849060DE0184FC069C10D018011086ACBEAB54AC0FB1F955D52BE889090754843060A1BC0C27D034E062EAA896A66895E919A087B88878EFFFFA20D081F1F5FF414224A2FFB110342053FB83A8A6ADB85A740314843060A1FC0C27D034E069B806030111F060BE4218309F9E069A0E4E73771927040D0860C143F8184FA069C0E468995035543F8A64F0F55AA49C2DC78905F47E0A41F0F62E3D500C3013D370400505060BDC4B0613F4720140800C0445060BD84B0613F2034D040C0068302F00C14478184F9069C0C03018179060A1BC0C27D034E06980983001B048A0C17B8960C27E834E060800C0445060BD84B0613F2034D0401D82583050DE0613EC1A704040A0C07C83050EA8184FA069C103D84180F6124183780613F01A7041A0401FD12C48F2BFCB3FD954B67087421830510A8184F9069C1434B87B807550F134FA92877FFF86E04281047E0A01F7D5E296244EF020B2F1EE01CF0F6AD7EA0B71FFFFE9E240301100C1BC8410613EE034D06244081D80C0BC8410613F41A70300ED0860C1432A0613E41A70400E0605E418285540C27D034E07FF572E56ABFF68BFE5C05147408C783001A01A10D58903C519DD6C437078C82807E0C17B0FC080940D3420C82807E0C17B0FC080940D342218301DE3F58B90034EFA880C0778FD62E400D3BE7FFFB1807006093449F5B6496560437A6082AEFE598A3A067BA90982B122821ED1E51F6017DD0697F8038181770322520F834DFC9BFACD992DF246C672344E01C0C0BB81912907C1A6FEE58DE6E18542EA94552A47BFC053714256B4665C5F07FC503CE81980D3BF280521001817703224010F834DFB8D1D58097C5EA70757D01A3FE50094200302F20A71F010F834DFC5CA87BD1EA86C449852F5806557D488988C4372605B0855977D436A9517815DE20FB8206843C6C7C82834DFDF00C1287E24FBF7FF55CB9C9457FF571287F73018105F97A4CE0D4B95D2F0527A7D2FD4143C3C7C0FFC1828B12C18506069BF47C0FFC1828A12C18505069BF4647A104182862E0613E81A7041B1E841060A18B8184FAA0D341FFFFA78903E577E0C102FCB80A8F018602FBE3C2145610D91F1760307F60D381FBEA707536ADB85B94608C0A5E1B0B381728150A3F54317511E4406E4FFFA8D03954C48A0A5F7F230306050A5DDF715571F977BD41827C545C947A511E3F55E2E052ABBE49E5252EFFAB8070432F56AB0780C2C1FD5834B0424097EDC0605EC10808741A4FE0F310603DC49031F0612241A0792FA1044BC89C183080681E888780C1B92B0613F44A069A0C72AA1D40605DFF1B03AA47C3B4A3AC407BFFFC3CCC180F71240C7C184890681E4A3E10CBD42ACDDAD8302100D03D118F018372560C27E8940D341928F018372560C27E8940D341FFFFB2082583343E19A45E830706330601C8180872E505D6677341817A128A8C0300E53E0C186F8184831F834DFC1B808F0923F56ABE3A9BC060A18BCBCB43201162F9DE893F2E4C3A90B5DFFFE98138545881EAAB03BC400D0BF393035E87CFEA440FF08A23812277262B60A70697F818073084251717F80EFE0309F5E545801736AE30086252B060C1D50FD3D8AC1860200B0DC084C4957F54A94F9BD8227D5962A3A041A3F118181FC0424F2C831FFFF570411207C5CAF770184FAF2A0697F4218FBF603040FE1F8158AF8043EEFFAB83001C082AE79429F0FBED8303F92D2A8F06003803448F896A1428E019C061A08330F490603E5503050E0180C24197834D0648AAC2F00ED03C3EBCFD1E28A07B89AFB4648C0C0470920647C0AA069A145C211752E12BCABF9B35A6BA8A0C3FFFC3D3010CB818287060034184842F069A193C180F92E060A1C100184832F069A1903E0C007892A6A8A2528DE72030D08F4806023820AE2520F034D09FFFE1E7601CAE0FC7F241F04099D1D172BD2D55E7188068FD52A2FCA5C255E365FEFDA59E79200604256252952A18C502138B820807178FD5A99554E3380D3417FFF87A60301F05C0C143006030908AC1A68408542379A68441191122283011C248191F20F034D06163EF282EF796921CFFFF4C04C20A9503F6C49C4C0D0FD8301EC25F9517F4180F7FF01827C55FD178432FFCF97D57DED2108610CBE0FFF3F07A2361D4C22108180F72E0530FC1848500B038252BAAA81ED4F7D14906EB4028BCBAE7E5AC0345049002E250301102403050C3E06121032122F84A1287704A9A0587C0C3410641B0E1E1E9A0C07C781828600C0612155834D083C0C006AB8AD57F3C5DE1D83040B3F202AE39101808E12414E5C83C0D3428E0060FBE252AB15D96DBBC061E0832FFFF0DC0870107E3C1F6A855F1DCE17CBF8CAD29D011800D2EF8FCBB3D823EF64A571FFFFE1E9C0C07C4060A182003090AAC1A6832106003BF3EABF62A1F2AE83040B2C43BE0C9081808D1241830506140C1A6841D0401F2B1F7BF15D976CCC109DFFFFFF570600501807000DF8401E28F974DD037E2FFAC0D141AAF972A5623FBDF4B0FA78300520C0750FC14E2403090A10C5A01808025D12448F17DCBAB4BE6937CE8900C0440F8182852E06920BDE93D324C8481A12841F806CF83020D0B9401754AFE0C2C11C3D1E8300DAB0F8184850681EC0C0BFFFD204C7C250F40F403F83D6544B202ACEEAD43C8F4A409F1B6B0B45D8D448EC0B1E99C332E04100D00C085F55E1EE6AADBAA69539EABE3E2E57DFFA3D70174106710A13ED7511087AF8301D2258305F8108184870681DC9C18011083F00F2EFFFFF556C56A00E2A56AB38CC56745418089120182852E06120DC12A9579508DE9E490F7FFF86E04460C038093F0803E1EF879B6A8D57E57F2C5474081BEABCAE67FD3F13C3FFFFE1EC40C0750960642103090C0D03C1A8300200C0390301F23D85EACB94FB38A952B5501857C6E910A0301122403050AA8184830C84818088120182852E06120DDFFFFFF5704000D2F1FAA1E0212B129537A0A145878B8BC7C07D9FC0298531E9E0C00E0301EE0820C17B82021704208155FC7D297FD57FFDE970343040180C045405397830A060D341AA55E5133B5289AAA8943FA24CA0A2545D62F4BF2A03C3E563E1ED0607F0BE269985B1E99C1800D5210590422E0691F16890E8400602260305F63F06140C1A68318AA0066240FD5ABF51D0F01A47E55F5568304F65FE02966153D3700C1E80683050C240309F7F1649B914B6CE349080210301100C1BD0FD0F81A6825B0DAB65C3FFABF780E286C1A27EF7E68297EA80A09C3D04180F8041060BE01006A380805E5E3F55FA5EAC7CAEA9C060415A211A06022E0305F63F06140C1A6830C2E2E03DBDB757C288EFFFF0CC04080407FFFC3CE4BC4B0601CCBAA8F896019EA05F8710E04100DD80C0BC02074941EAAF56E38581808980C17E1782A81A682FFFFFFFFFFFFFFFFFFF
		 */
	}
}
