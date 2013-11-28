package com.ttProject.media.vp6.test;

import org.junit.Test;

import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

public class FileAnalyzeTest {
	@Test
	public void test() throws Exception {
		IFileReadChannel channel = FileReadChannel.openFileReadChannel("http://red5.googlecode.com/svn-history/r4071/java/example/trunk/oflaDemo/www/streams/toystory3-vp6.flv");
		channel.close();
	}
}
