package com.ttProject.media.mpegts.test;

import org.junit.Test;

import com.ttProject.media.mpegts.IPacketAnalyzer;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.PacketAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mpegtsのファイル解析テスト
 * @author taktod
 *
 */
public class FileAnalyzeTest {
	/**
	 * 固定ファイル用
	 */
	@Test
	public void fixedFileTest() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel("~/tmp/mario.ts");
		IPacketAnalyzer analyzer = new PacketAnalyzer();
		Packet packet = null;
		while((packet = analyzer.analyze(source)) != null) {
			System.out.println(packet);
		}
		source.close();
	}
}
