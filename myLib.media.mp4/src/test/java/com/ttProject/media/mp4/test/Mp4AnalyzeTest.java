package com.ttProject.media.mp4.test;

import org.junit.Test;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.AtomAnalyzer;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

public class Mp4AnalyzeTest {
	@Test
	public void test() {
		try {
			IFileReadChannel fc = FileReadChannel.openFileReadChannel("http://www.gomplayer.jp/img/sample/mp4_h264_aac.mp4");
			IAtomAnalyzer analyzer = new AtomAnalyzer();
			Atom atom = null;
			while((atom = analyzer.analyze(fc)) != null) {
				System.out.println(atom);
			}
			fc.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
