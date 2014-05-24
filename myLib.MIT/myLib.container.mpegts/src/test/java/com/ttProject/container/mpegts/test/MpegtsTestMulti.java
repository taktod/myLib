/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mpegts.CodecType;
import com.ttProject.container.mpegts.MpegtsPacketReader;
import com.ttProject.container.mpegts.MpegtsPacketWriter;
import com.ttProject.container.mpegts.field.PmtElementaryField;
import com.ttProject.container.mpegts.field.PmtElementaryFieldFactory;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mpegtsの動作テスト
 * @author taktod
 */
public class MpegtsTestMulti {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsTestMulti.class);
	@Test
	public void test() throws Exception {
		logger.info("test開始");
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/mario_3video_1audio.ts" // ソースとして、3つのvideoトラック 1つのaudioトラックをもつmpegtsのデータをあてておく
			)
		);
	}
	private void analyzerTest(IFileReadChannel source) {
		MpegtsPacketWriter writer1 = null;
		MpegtsPacketWriter writer2 = null;
		MpegtsPacketWriter writer3 = null;
		MpegtsPacketWriter writer4 = null;
		PmtElementaryFieldFactory factory = new PmtElementaryFieldFactory();
		PmtElementaryFieldFactory factory_audio = new PmtElementaryFieldFactory();
		try {
			writer1 = new MpegtsPacketWriter("output_640x360.ts");
			writer2 = new MpegtsPacketWriter("output_320x180.ts");
			writer3 = new MpegtsPacketWriter("output_160x90.ts");
			writer4 = new MpegtsPacketWriter("output_audio_only.ts");
			Sdt sdt = new Sdt();
			sdt.writeDefaultProvider("test", "hpgehoge");
			writer1.addContainer(sdt);
			writer2.addContainer(sdt);
			writer3.addContainer(sdt);
			writer4.addContainer(sdt);
			Pat pat = new Pat();
			writer1.addContainer(pat);
			writer2.addContainer(pat);
			writer3.addContainer(pat);
			writer4.addContainer(pat);
			Pmt pmt = new Pmt(pat.getPmtPid());
			PmtElementaryField videoElementaryField = factory.makeNewField(CodecType.VIDEO_H264);
			pmt.addNewField(videoElementaryField);
			pmt.setPcrPid(videoElementaryField.getPid());
			PmtElementaryField audioElementaryField = factory.makeNewField(CodecType.AUDIO_AAC);
			pmt.addNewField(audioElementaryField);
			writer1.addContainer(pmt);
			writer2.addContainer(pmt);
			writer3.addContainer(pmt);
			
			pmt = new Pmt(pat.getPmtPid());
			PmtElementaryField audioElementaryField2 = factory_audio.makeNewField(CodecType.AUDIO_AAC);
			pmt.addNewField(audioElementaryField2);
			pmt.setPcrPid(audioElementaryField2.getPid());
			writer4.addContainer(pmt);

			IReader reader = new MpegtsPacketReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof Pes) {
					Pes pes = (Pes)container;
					logger.info(pes);
					switch(pes.getPid()) {
					case 0x0100: // track1
						if(pes.getFrame() != null) {
							writer1.addFrame(videoElementaryField.getPid(), pes.getFrame());
						}
						break;
					case 0x0101: // track2
						if(pes.getFrame() != null) {
							writer2.addFrame(videoElementaryField.getPid(), pes.getFrame());
						}
						break;
					case 0x0102: // track3
						if(pes.getFrame() != null) {
							writer3.addFrame(videoElementaryField.getPid(), pes.getFrame());
						}
						break;
					case 0x0103: // audioTrack
						if(pes.getFrame() != null) {
							writer1.addFrame(audioElementaryField.getPid(), pes.getFrame());
							writer2.addFrame(audioElementaryField.getPid(), pes.getFrame());
							writer3.addFrame(audioElementaryField.getPid(), pes.getFrame());
							writer4.addFrame(audioElementaryField2.getPid(), pes.getFrame());
						}
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
