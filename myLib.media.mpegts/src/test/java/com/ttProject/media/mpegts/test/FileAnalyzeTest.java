package com.ttProject.media.mpegts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.mpegts.IPacketAnalyzer;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.PacketAnalyzer;
import com.ttProject.media.mpegts.field.AdaptationField;
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
				"/Users/todatakahiko/Documents/workspace_mvn/myLib/myLib.packet.mpegts/test_made.ts"
		);
/*		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("mario.ts")
		);*/
		IPacketAnalyzer analyzer = new PacketAnalyzer();
		Packet packet = null;
		int counter = 0;
		while((packet = analyzer.analyze(source)) != null) {
			if(packet instanceof Pes) {
				Pes pes = (Pes) packet;
				if(pes.isPayloadUnitStart()) {
					if(pes.isAdaptationFieldExist()) {
						AdaptationField aField = pes.getAdaptationField();
						if(aField.hasPcr()) {
							logger.info("pcr:" + (aField.getPcrBase() / 90000D));
							logger.info(pes.getPts());
						}
					}
					if(pes.hasPts()) {
						if(pes.getPid() == 0x0101) {
							logger.info(pes.getPesPacketLength());
							logger.info(counter);
							counter = 0;
							logger.info("*" + pes.getPts());
						}
						else {
//							logger.info(pes.getPts());
						}
					}
				}
				if(pes.getPid() == 0x0101) {
					counter ++;
				}
			}
		}
		source.close();
	}
}
