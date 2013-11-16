package com.ttProject.media.mpegts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.mpegts.IPacketAnalyzer;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.PacketAnalyzer;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.media.mpegts.packet.Pmt;
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
		try {
			IReadChannel source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("rtypeDelta.ts")
			);
			IPacketAnalyzer analyzer = new PacketAnalyzer();
			Packet packet = null;
//			int counter = 0;
			while((packet = analyzer.analyze(source)) != null) {
				if(packet instanceof Pmt) {
					System.out.println(packet);
				}
				if(packet instanceof Pes) {
					Pes pes = (Pes) packet;
					if(pes.isPayloadUnitStart()) {
						logger.info(pes);
					}
				}
			}
			source.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
