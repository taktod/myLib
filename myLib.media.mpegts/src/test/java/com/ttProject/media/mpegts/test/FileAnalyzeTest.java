package com.ttProject.media.mpegts.test;

import org.junit.Test;

import com.ttProject.media.mpegts.IPacketAnalyzer;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.PacketAnalyzer;
import com.ttProject.media.mpegts.packet.Pes;
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
		IReadChannel source = FileReadChannel.openFileReadChannel(
				"/Users/todatakahiko/tmp/mario.nosound.ts"
		);
		IPacketAnalyzer analyzer = new PacketAnalyzer();
		Packet packet = null;
		while((packet = analyzer.analyze(source)) != null) {
			if(packet instanceof Pes) {
				System.out.println(packet);
			}
		}
		source.close();
	}
}
