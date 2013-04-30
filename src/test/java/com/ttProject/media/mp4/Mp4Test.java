package com.ttProject.media.mp4;

import java.net.URL;

import org.junit.Test;

import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.URLFileReadChannel;

public class Mp4Test {
	@Test
	public void mp4Analize() throws Exception {
		URL url = Thread.currentThread().getContextClassLoader().getResource("test.mp4");
		IFileReadChannel fc = new FileReadChannel(url);
		IAtomAnalyzer analyzer = new AtomAnalyzer();
		Atom atom = null;
		while((atom = analyzer.analize(fc)) != null) {
			System.out.println(atom);
		}
		fc.close();
	}
	@Test
	public void mp4AnalizeForURL() throws Exception {
		IFileReadChannel fc = new URLFileReadChannel("http://localhost/test.mp4");
		IAtomAnalyzer analyzer = new AtomAnalyzer();
		Atom atom = null;
		while((atom = analyzer.analize(fc)) != null) {
			System.out.println(atom);
		}
		fc.close();
	}
}
