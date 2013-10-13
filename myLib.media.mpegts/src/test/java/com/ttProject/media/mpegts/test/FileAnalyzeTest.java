package com.ttProject.media.mpegts.test;

import org.apache.log4j.Logger;
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
	private Logger logger = Logger.getLogger(FileAnalyzeTest.class);
	/**
	 * 固定ファイル用
	 */
	@Test
	public void fixedFileTest() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.ts")
		);
		IPacketAnalyzer analyzer = new PacketAnalyzer();
		Packet packet = null;
		while((packet = analyzer.analyze(source)) != null) {
			if(packet instanceof Pes) {
				logger.info(packet);
			}
		}
		source.close();
	}
}
