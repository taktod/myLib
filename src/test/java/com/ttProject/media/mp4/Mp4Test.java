package com.ttProject.media.mp4;

import org.junit.Test;

import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

public class Mp4Test {
	@Test
	public void mp4Analize() throws Exception {
		IFileReadChannel fc = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.mp4");
		IAtomAnalyzer analyzer = new AtomAnalyzer();
		Atom atom = null;
		while((atom = analyzer.analyze(fc)) != null) {
			System.out.println(atom);
		}
		fc.close();
	}
}
