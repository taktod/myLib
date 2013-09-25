package com.ttProject.media.mp4.test;

import org.junit.Test;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.AtomAnalyzer;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * ファイルの読み込みテスト
 * @author taktod
 *
 */
public class FileAnalyzeTest {
	/**
	 * 固定ファイルの読み込みテスト
	 */
	@Test
	public void fixedFileTest() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.mp4")
		);
		IAtomAnalyzer analyzer = new AtomAnalyzer();
		Atom atom = null;
		while((atom = analyzer.analyze(source)) != null) {
			System.out.println(atom);
		}
		source.close();
	}
	/**
	 * 追記されているデータ読み込みテスト(mp4ではありえないので、やらない(moofのあるデータならあり得るかも))
	 */
	@Test
	public void appendingBufferTest() throws Exception {
		
	}
}
