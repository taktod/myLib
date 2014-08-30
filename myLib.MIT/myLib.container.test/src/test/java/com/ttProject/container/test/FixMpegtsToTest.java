/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.test;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.adts.AdtsUnit;
import com.ttProject.container.adts.AdtsUnitReader;
import com.ttProject.container.mpegts.MpegtsCodecType;
import com.ttProject.container.mpegts.MpegtsPacketReader;
import com.ttProject.container.mpegts.MpegtsPacketWriter;
import com.ttProject.container.mpegts.field.PmtElementaryField;
import com.ttProject.container.mpegts.field.PmtElementaryFieldFactory;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.frame.Frame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * make mpegts from 2 files.
 * @author taktod
 */
public class FixMpegtsToTest {
	/** logger */
	private Logger logger = Logger.getLogger(FixMpegtsToTest.class);
//	@Test
	public void test() throws Exception {
		logger.info("mixed out.h264.ts and out.aac");
		IFileReadChannel h264Source = FileReadChannel.openFileReadChannel("out.h264.ts");
		IFileReadChannel aacSource = FileReadChannel.openFileReadChannel("out.aac");
		MpegtsPacketWriter writer = null;
		try {
			PmtElementaryFieldFactory factory = new PmtElementaryFieldFactory();
			IReader mpegtsReader = new MpegtsPacketReader();
			IReader adtsReader = new AdtsUnitReader();
			writer = new MpegtsPacketWriter("fixed.ts");
			// make sdt
			Sdt sdt = new Sdt();
			sdt.writeDefaultProvider("taktodTools", "mpegtsMuxer");
			writer.addContainer(sdt);
			// make pat
			Pat pat = new Pat();
			writer.addContainer(pat);
			// make pmt
			Pmt pmt = new Pmt(pat.getPmtPid());
			PmtElementaryField h264Field = factory.makeNewField(MpegtsCodecType.VIDEO_H264);
			pmt.setPcrPid(h264Field.getPid());
			pmt.addNewField(h264Field);
			PmtElementaryField aacField = factory.makeNewField(MpegtsCodecType.AUDIO_AAC);
			pmt.addNewField(aacField);
			writer.addContainer(pmt);
			
			IContainer container = null;
			IFrame frame = null;
			long h264TimeDiff = 0; // mpegts data will not start with pts = 0, deal with diff
			
			while((container = mpegtsReader.read(h264Source)) != null) {
				if(container instanceof Pes) {
					Pes pes = (Pes)container;
					frame = pes.getFrame();
					if(frame == null) {
						continue;
					}
					logger.info(frame);
					if(h264TimeDiff == 0) {
						h264TimeDiff = frame.getPts();
					}
					// resetup pts, to make frame from pts = 0
					if(frame instanceof VideoMultiFrame) {
						((VideoMultiFrame) frame).setPts(frame.getPts() - h264TimeDiff);
						for(IVideoFrame vFrame : ((VideoMultiFrame) frame).getFrameList()) {
							if(vFrame.getPts() - h264TimeDiff < 0) {
								((Frame)vFrame).setPts(0);
							}
							else {
								((Frame)vFrame).setPts(vFrame.getPts() - h264TimeDiff);
							}
						}
					}
					else {
						((Frame)frame).setPts(frame.getPts() - h264TimeDiff);
					}
					writer.addFrame(h264Field.getPid(), frame);
					while((container = adtsReader.read(aacSource)) != null) {
						if(container instanceof AdtsUnit) {
							AdtsUnit adts = (AdtsUnit) container;
							// pick up aac frame
							IFrame aacFrame = adts.getFrame();
							if(aacFrame == null) {
								continue;
							}
							logger.info(aacFrame);
							writer.addFrame(aacField.getPid(), aacFrame);
							if(1.0f * aacFrame.getPts() / aacFrame.getTimebase() > 1.0f * (frame.getPts() - h264TimeDiff) / frame.getTimebase()) {
								break;
							}
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("exception occured", e);
		}
		finally {
			// close mpegtsWriter
			if(writer != null) {
				try {
					writer.prepareTailer();
				}
				catch(Exception e) {
				}
				writer = null;
			}
			// close sources
			if(h264Source != null) {
				try {
					h264Source.close();
				}
				catch(Exception e) {
				}
				h264Source = null;
			}
			if(aacSource != null) {
				try {
					aacSource.close();
				}
				catch(Exception e) {
				}
				aacSource = null;
			}
		}
	}
}
