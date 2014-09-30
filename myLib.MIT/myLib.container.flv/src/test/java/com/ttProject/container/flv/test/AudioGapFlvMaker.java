package com.ttProject.container.flv.test;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.flv.FlvHeaderTag;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * エラーのあるflvをつくってどうなるか試してみる
 * @author taktod
 */
@SuppressWarnings("resource")
public class AudioGapFlvMaker {
	/** ロガー */
	private Logger logger = Logger.getLogger(AudioGapFlvMaker.class);
	/**
	 * 音声の変なファイル作成
	 * @throws Exception
	 */
//	@Test
	public void audio() throws Exception {
		logger.info("try to make audio flv with timestamp gap.");
		IFileReadChannel source = FileReadChannel.openFileReadChannel("longvp6mp3.flv");
		FlvTagReader reader = new FlvTagReader();
		FileChannel audioOutput0 = new FileOutputStream("gapped.flv").getChannel(); // 通常
		FlvHeaderTag headerTag = new FlvHeaderTag();
		headerTag.setAudioFlag(true);
		headerTag.setVideoFlag(false);
		audioOutput0.write(headerTag.getData());
		IContainer container = null;
		long lastPts = 0;
		while((container = reader.read(source)) != null) {
//			logger.info("info:" + container.toString());
			if(container instanceof AudioTag) {
//				Thread.sleep(100);
				AudioTag aTag = (AudioTag)container;
//				audioOutput0.write(aTag.getData());
				if(aTag.getPts() < 5000 || aTag.getPts() > 40000) {
					audioOutput0.write(aTag.getData());
					lastPts = aTag.getPts();
//					logger.info("updateLastPts:" + lastPts);
				}
				else {
					if(aTag.getPts() > lastPts + 1000) {
						logger.info("insert:" + (lastPts + 1000));
						int channels = aTag.getChannels();
						int sampleRate = aTag.getSampleRate();
						IAudioFrame insertFrame = null;
						switch(aTag.getCodec()) {
						case AAC:
							insertFrame = AacFrame.getMutedFrame(sampleRate, channels, 16);
							break;
						case MP3:
							insertFrame = Mp3Frame.getMutedFrame(sampleRate, channels, 16);
							break;
						default:
							throw new RuntimeException("this is unexpected frame:" + aTag.getCodec());
						}
						aTag = new AudioTag();
						aTag.addFrame(insertFrame);
						aTag.getData();
						aTag.setPts(lastPts + 1000);
						audioOutput0.write(aTag.getData());
						lastPts = aTag.getPts();
						logger.info("updateLastPts:" + lastPts);
					}
				}
			}
		}
		logger.info("end");
		source.close();
		audioOutput0.close();
	}
}
