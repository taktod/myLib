package com.ttProject.xuggle.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.junit.Test;

import com.ttProject.xuggle.test.swing.TestFrame;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

/**
 * xuggleで動画の再生を実行してみるテスト
 * @author taktod
 */
public class PlayTest {
	/**
	 * 一番簡単にできるやりかたはコレ
	 * xuggleに付いている一番楽な方法で再生するだけ。
	 * @throws Exception
	 */
//	@Test
	public void playTest() throws Exception {
		IMediaReader mediaReader = ToolFactory.makeReader("mario.mp4");
		mediaReader.addListener(ToolFactory.makeViewer());
		while(mediaReader.readPacket() == null) {
			;
		}
	}
	/**
	 * とりあえずテストとして、audioをplayさせてみようか・・・
	 * 基本的にはjavaSoundAPIをつかって、生データに戻しつつ、再生デバイスに流し込めばいいはず。
	 * xuggleをつかってrawDataに戻しつつ流し込めばいいのか？
	 * @see http://xuggle.googlecode.com/svn/trunk/java/xuggle-xuggler/src/com/xuggle/xuggler/demos/DecodeAndPlayAudio.java
	 * @throws Exception
	 */
//	@Test
	public void playTest2() throws Exception {
		SourceDataLine audioLine = null;
		IContainer container = IContainer.make();
		if(container.open("/Users/todatakahiko/tmp/mario.mp4", IContainer.Type.READ, null) < 0) {
			throw new Exception("ファイルがひらけませんでした。");
		}
		int numStreams = container.getNumStreams();
		int audioStreamId = -1;
		IStreamCoder audioCoder = null;
		for(int i = 0;i < numStreams;i ++) {
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			if(coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				audioStreamId = i;
				audioCoder = coder;
				break;
			}
		}
		if(audioStreamId == -1) {
			throw new Exception("音声用のトラックがみつからなかった。");
		}
		if(audioCoder.open(null, null) < 0) {
			throw new Exception("音声用のデコーダが開けなかった。");
		}
		AudioFormat audioFormat = new AudioFormat(audioCoder.getSampleRate(),
				(int)IAudioSamples.findSampleBitDepth(audioCoder.getSampleFormat()),
				audioCoder.getChannels(), true /* 16bit samples */, false);
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
		
		// メディアデータを開いていく。
		IPacket packet = IPacket.make();
		while(container.readNextPacket(packet) >= 0) {
			if(packet.getStreamIndex() == audioStreamId) {
				// audioデータの場合
				IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());
				int offset = 0;
				while(offset < packet.getSize()) {
					int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
					if(bytesDecoded < 0) {
						throw new Exception("デコード中にエラーが発生");
					}
					offset += bytesDecoded;
					if(samples.isComplete()) {
						// 再生にまわす。
						byte[] rawBytes = samples.getData().getByteArray(0, samples.getSize());
						audioLine.write(rawBytes, 0, samples.getSize());
					}
				}
			}
		}
		// あとしまつ。
		if(audioLine != null) {
			audioLine.drain();
			audioLine.close();
			audioLine = null;
		}
		if(audioCoder != null) {
			audioCoder.close();
			audioCoder = null;
		}
		if(container != null) {
			container.close();
			container = null;
		}
	}
	/**
	 * 再生テスト
	 * 映像を流す。
	 * @see http://xuggle.googlecode.com/svn/trunk/java/xuggle-xuggler/src/com/xuggle/xuggler/demos/VideoImage.java
	 * @see http://xuggle.googlecode.com/svn/trunk/java/xuggle-xuggler/src/com/xuggle/xuggler/demos/DecodeAndPlayVideo.java
	 * @throws Exception
	 */
	private boolean running = true;
//	@Test
	public void playTest3() throws Exception {
		// つうかこれ、swingを使わないとどうにもならんだろ・・・
		TestFrame frame = new TestFrame();
		// TODO このexit on closeはテスト動作では、有効にならないみたいです。
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				running = false;
			}
		});
		// ちょっと描画テストやってみた。特に問題なさそう。あとは適当にデータの書き込みテストをつくっておけばいいかな。
/*		BufferedImage base = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
		String message = DateUtil.makeDateTime();
		Graphics g = base.getGraphics();
		g.setColor(Color.white);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
		g.drawString(message, 100, 100);
		g.dispose();
		frame.getVideoComponent().setImage(base); */
		
		// xuggleをつかって動画データを読み込んでみる。
		if(!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
			throw new Exception("問題の動作をさせたいんですが、サンプリング動作がサポートされていません。GPLバージョンのxugglerをいれなきゃいけないらしい。");
		}
		IContainer container = IContainer.make();
		if(container.open("mario.mp4", IContainer.Type.READ, null) < 0) {
			throw new Exception("ファイルを開くことができませんでした。");
		}
		int numStreams = container.getNumStreams();
		int videoStreamId = -1;
		IStreamCoder videoCoder = null;
		for(int i = 0;i < numStreams;i ++) {
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			if(coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				videoStreamId = i;
				videoCoder = coder;
				break;
			}
		}
		if(videoStreamId == -1) {
			throw new Exception("映像用のトラックがありませんでした。");
		}
		if(videoCoder.open(null, null) < 0) {
			throw new Exception("映像用のデコーダーが開けませんでした。");
		}
		IVideoResampler resampler = null;
		if(videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
			// BGR24じゃなかったらpixelの書き換えが必須。
			resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
			if(resampler == null) {
				throw new Exception("pixelリサンプラーが取得できませんでした。");
			}
		}
		
		IPacket packet = IPacket.make();
		long firstTimestampInStream = Global.NO_PTS;
		long systemClockStartTime = 0;
		while(container.readNextPacket(packet) >= 0 && running) {
			// パケットデータが読み込めた場合
			if(packet.getStreamIndex() == videoStreamId) {
				// 動画データの場合
				IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
				int offset = 0;
				while(offset < packet.getSize()) {
					int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
					if(bytesDecoded < 0) {
						throw new Exception("デコード中に問題が発生しました。");
					}
					offset += bytesDecoded;
					if(picture.isComplete()) {
						IVideoPicture newPic = picture;
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
						BufferedImage javaImage = Utils.videoPictureToImage(newPic);
						
						frame.getVideoComponent().setImage(javaImage);
					}
				}
			}
		}
		if(videoCoder != null) {
			videoCoder.close();
			videoCoder = null;
		}
		if(container != null) {
			container.close();
			container = null;
		}
		while(running) {
			Thread.sleep(100);
		}
	}
}
