package com.ttProject.container.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.flv.FlvHeaderTag;
import com.ttProject.container.flv.FlvTagWriter;
import com.ttProject.container.mpegts.MpegtsPacketReader;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mpegtsのデータを別のコンテナに変換する動作テスト
 * @author taktod
 */
public class MultiMpegtsToTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MultiMpegtsToTest.class);
	/**
	 * マルチトラックのmpegtsからflvファイルを複数同時に作成する動作テスト
	 * @throws Exception
	 */
	@Test
	public void makeMultiFlvTest() throws Exception {
		logger.info("複数トラックのmpegtsからflvを作ります。");
		convertTest(
			FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/mario_3video_1audio.ts"
			)
		);
	}
	private void convertTest(IFileReadChannel source) {
		FlvTagWriter writer1 = null;
		FlvTagWriter writer2 = null;
		FlvTagWriter writer3 = null;
		FlvTagWriter writer4 = null;
		try {
			writer1 = new FlvTagWriter("output_640x360.flv");
			writer2 = new FlvTagWriter("output_320x180.flv");
			writer3 = new FlvTagWriter("output_160x90.flv");
			writer4 = new FlvTagWriter("output_audio_only.flv");
			FlvHeaderTag flvHeader = new FlvHeaderTag();
			flvHeader.setAudioFlag(true);
			flvHeader.setVideoFlag(true);
			writer1.addContainer(flvHeader);
			writer2.addContainer(flvHeader);
			writer3.addContainer(flvHeader);
			flvHeader.setVideoFlag(false);
			writer4.addContainer(flvHeader);
			IReader reader = new MpegtsPacketReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof Pes) {
					Pes pes = (Pes)container;
					logger.info(pes);
					if(pes.getFrame() == null) {
						continue;
					}
					switch(pes.getPid()) {
					case 0x0100:
						writer1.addFrame(pes.getPid(), pes.getFrame());
						break;
					case 0x0101:
						writer2.addFrame(pes.getPid(), pes.getFrame());
						break;
					case 0x0102:
						writer3.addFrame(pes.getPid(), pes.getFrame());
						break;
					case 0x0103:
						writer1.addFrame(pes.getPid(), pes.getFrame());
						writer2.addFrame(pes.getPid(), pes.getFrame());
						writer3.addFrame(pes.getPid(), pes.getFrame());
						writer4.addFrame(pes.getPid(), pes.getFrame());
						break;
					default:
						break;
					}
				}
			}
		}
		catch(Exception e) {
			logger.warn("例外発生", e);
		}
		finally {
			if(writer1 != null) {
				try {
					writer1.prepareTailer();
				}
				catch(Exception e) {}
				writer1 = null;
			}
			if(writer2 != null) {
				try {
					writer2.prepareTailer();
				}
				catch(Exception e) {}
				writer2 = null;
			}
			if(writer3 != null) {
				try {
					writer3.prepareTailer();
				}
				catch(Exception e) {}
				writer3 = null;
			}
			if(writer4 != null) {
				try {
					writer4.prepareTailer();
				}
				catch(Exception e) {}
				writer4 = null;
			}
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {}
				source = null;
			}
		}
	}
}
