package com.ttProject.media.mp3.test;

import org.junit.Test;

import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.FrameAnalyzer;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mp3の解析テスト
 * @author taktod
 */
public class Mp3AnalyzeTest {
	/**
	 * mp3ファイルを解析して、内容をdumpしてみる。
	 */
	@Test
	public void test() {
		try {
			IFileReadChannel channel = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("sample.mp3")
			);
			IFrameAnalyzer analyzer = new FrameAnalyzer();
			Frame frame = null;
			while((frame = analyzer.analyze(channel)) != null) {
				System.out.println(frame);
				// いままでなら、analyzerの中でこのchannel.positionもやっていたけど・・・
				// そこが不満だ・・・
				channel.position(frame.getPosition() + frame.getSize());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
