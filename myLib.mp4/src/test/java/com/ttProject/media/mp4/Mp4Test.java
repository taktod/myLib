package com.ttProject.media.mp4;

import org.junit.Test;

import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

public class Mp4Test {
	@Test
	public void mp4Analize() throws Exception {
		try {
			IReadChannel fc = FileReadChannel.openFileReadChannel("http://www.gomplayer.jp/img/sample/mp4_h264_aac.mp4");
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
