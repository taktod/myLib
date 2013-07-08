package com.ttProject.media.mp3.test;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.ttProject.media.mp3.Frame;
import com.ttProject.media.mp3.FrameAnalyzer;
import com.ttProject.media.mp3.IFrameAnalyzer;
import com.ttProject.media.mp3.Mp3Manager;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * mp3の解析テスト
 * @author taktod
 */
public class Mp3AnalyzeTest {
	/**
	 * mp3ファイルを解析して、内容をdumpしてみる。
	 */
//	@Test
	public void test() {
		try {
			IReadChannel channel = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("sample.mp3")
			);
			IFrameAnalyzer analyzer = new FrameAnalyzer();
			Frame frame = null;
			while((frame = analyzer.analyze(channel)) != null) {
				System.out.println(frame);
			}
			channel.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test2() {
		try {
			IReadChannel source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("sample.mp3")
			);
			ByteBuffer buffer = BufferUtil.safeRead(source, 2560);
			Mp3Manager manager = new Mp3Manager();
			for(Frame tag : manager.getUnits(buffer)) {
				System.out.println(tag);
			}
			buffer = BufferUtil.safeRead(source, 256);
			for(Frame tag : manager.getUnits(buffer)) {
				System.out.println(tag);
			}
			buffer = BufferUtil.safeRead(source, 256);
			for(Frame tag : manager.getUnits(buffer)) {
				System.out.println(tag);
			}
			source.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
