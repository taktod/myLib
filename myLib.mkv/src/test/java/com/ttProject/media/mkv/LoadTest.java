package com.ttProject.media.mkv;

import org.junit.Test;

import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * h.264のmshはcodecPrivateの中にはいっていた。
 * aacのmshもcodecPrivateの中にはいっていた。
 * @author taktod
 *
 */
public class LoadTest {
	@Test
	public void test() throws Exception {
		IFileReadChannel channel = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("testffmpeg.webm")
		);
		IElementAnalyzer analyzer = new ElementAnalyzer();
		// とりあえず中身をしる必要があるので、しっていく
		Element e = null;
		while((e = analyzer.analyze(channel)) != null) {
			System.out.println(e);
		}
		System.out.println("おしまい");
	}
}
