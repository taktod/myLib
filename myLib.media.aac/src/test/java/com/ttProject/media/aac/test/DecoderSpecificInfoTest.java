package com.ttProject.media.aac.test;

import org.junit.Test;

import com.ttProject.media.aac.DecoderSpecificInfo;
import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.IFrameAnalyzer;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

public class DecoderSpecificInfoTest {
	@Test
	public void restore() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("1210"));
		DecoderSpecificInfo specificInfo = new DecoderSpecificInfo();
		specificInfo.analyze(channel);
		System.out.println(specificInfo);
	}
	@Test
	public void make() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("25-1.aac")
		);
		IFrameAnalyzer analyzer = new FrameAnalyzer();
		DecoderSpecificInfo specificInfo = new DecoderSpecificInfo();
		Frame frame = null;
		while((frame = analyzer.analyze(source)) != null) {
			System.out.println(frame);
			if(frame instanceof Aac) {
				specificInfo.analyze((Aac)frame);
				System.out.println(HexUtil.toHex(specificInfo.getInfoBuffer()));
				System.out.println(specificInfo);
				break;
			}
		}
		source.close();
	}
}
