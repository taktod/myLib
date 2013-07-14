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
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("mario.ts")
		);
		IPacketAnalyzer analyzer = new PacketAnalyzer();
		Packet packet = null;
		int i = 0;
		while((packet = analyzer.analyze(source)) != null) {
			System.out.println(packet);
			i ++;
			if(i > 20) {
				break;
			}
		}
		source.close();
	}
}
