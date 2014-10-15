/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.IWriter;
import com.ttProject.container.adts.AdtsUnitWriter;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.FlvTagWriter;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.container.mp3.Mp3UnitWriter;
import com.ttProject.container.mpegts.MpegtsCodecType;
import com.ttProject.container.mpegts.MpegtsPacketWriter;
import com.ttProject.container.mpegts.field.PmtElementaryField;
import com.ttProject.container.mpegts.field.PmtElementaryFieldFactory;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.container.ogg.OggPageWriter;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.HexUtil;

/**
 * convert container from flv to ?
 * @author taktod
 */
public class FlvToTest {
	/** logger */
	private Logger logger = Logger.getLogger(FlvToTest.class);
	/**
	 * to mp3.
	 * @throws Exception
	 */
//	@Test
	public void mp3() throws Exception {
		logger.info("from flv to mp3 test.");
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mp3.flv")
			),
			new Mp3UnitWriter("output.mp3"),
			0, 1
		);
	}
	/**
	 * to adts.
	 * @throws Exception
	 */
	@Test
	public void adts() throws Exception {
		logger.info("from flv to adts test");
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("aac.flv")
			),
			new AdtsUnitWriter("output.aac"),
			0, 1
		);
	}
	/**
	 * to ogg
	 * @throws Exception
	 */
	@Test
	public void ogg() throws Exception {
		OggPageWriter writer = new OggPageWriter("output.ogg");
		logger.info("from flv to ogg test");
		HeaderFrame headerFrame = new HeaderFrame();
		headerFrame.fillWithFlvDefault(1);
		writer.addFrame(1, headerFrame);
		writer.completePage(1);
		logger.info(headerFrame);
		logger.info(HexUtil.toHex(headerFrame.getData(), true));
		CommentFrame commentFrame = new CommentFrame();
		logger.info(HexUtil.toHex(commentFrame.getData(), true));
		writer.addFrame(1, commentFrame);
		writer.completePage(1);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("speex.flv")
			),
			writer,
			0, 1
		);
	}
	/**
	 * to mpegts(mp3)
	 * @throws Exception
	 */
	@Test
	public void mpegts_mp3() throws Exception {
		logger.info("from flv to mpegts test(mp3)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_mp3.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// need sdt pat pmt.
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		Pat pat = new Pat();
		writer.addContainer(pat);
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField elementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.AUDIO_MPEG1);
		pmt.addNewField(elementaryField);
		pmt.setPcrPid(elementaryField.getPid());
		writer.addContainer(pmt);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mp3.flv")
			),
			writer,
			0,
			elementaryField.getPid()
		);
	}
	/**
	 * to mpegts(aac)
	 * @throws Exception
	 */
	@Test
	public void mpegts_aac() throws Exception {
		logger.info("from flv to mpegts test(aac)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_aac.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// firstly need sdt pat pmt.
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		Pat pat = new Pat();
		writer.addContainer(pat);
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField elementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.AUDIO_AAC);
		pmt.setPcrPid(elementaryField.getPid());
		pmt.addNewField(elementaryField);
		writer.addContainer(pmt);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("aac.flv")
			),
			writer,
			0,
			elementaryField.getPid()
		);
	}
	/**
	 * to mpegts(h264)
	 * @throws Exception
	 */
	@Test
	public void mpegts_h264() throws Exception {
		logger.info("from flv to mpegts test(h264)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_h264.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// firstly need sdt pat pmt
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		Pat pat = new Pat();
		writer.addContainer(pat);
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField elementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.VIDEO_H264);
		pmt.setPcrPid(elementaryField.getPid());
		pmt.addNewField(elementaryField);
		writer.addContainer(pmt);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("h264.flv")
			),
			writer,
			elementaryField.getPid(),
			0
		);
	}
	/**
	 * to mpegts(h264 / aac)
	 * @throws Exception
	 */
	@Test
	public void mpegts_h264_aac() throws Exception {
		logger.info("from flv to mpegts test(h264 / aac)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_h264_aac.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// firstly need sdt pat pmt
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		Pat pat = new Pat();
		writer.addContainer(pat);
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField videoElementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.VIDEO_H264);
		pmt.setPcrPid(videoElementaryField.getPid());
		pmt.addNewField(videoElementaryField);
		PmtElementaryField audioElementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.AUDIO_AAC);
		pmt.addNewField(audioElementaryField);
		writer.addContainer(pmt);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("h264_aac.flv")
			),
			writer,
			videoElementaryField.getPid(),
			audioElementaryField.getPid()
		);
	}
	/**
	 * to mpegts(h264 / aac) (this h264 slice frame is constisted with 2 slice nals)
	 * from iphone 5S
	 * @throws Exception
	 */
	@Test
	public void mpegts_h264_aac_ex() throws Exception {
		logger.info("from flv to mpegts test(h264 / aac)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_h264_aac_ex.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// firstly need sdt pat pmt
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		Pat pat = new Pat();
		writer.addContainer(pat);
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField videoElementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.VIDEO_H264);
		pmt.setPcrPid(videoElementaryField.getPid());
		pmt.addNewField(videoElementaryField);
		PmtElementaryField audioElementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.AUDIO_AAC);
		pmt.addNewField(audioElementaryField);
		writer.addContainer(pmt);
		convertTest(
			FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/ahiru.flv"
			),
			writer,
			videoElementaryField.getPid(),
			audioElementaryField.getPid()
		);
	}
	/**
	 * to flv(mp3)
	 * @throws Exception
	 */
	@Test
	public void flv_mp3() throws Exception {
		logger.info("from flv to flv test(mp3)");
		FlvTagWriter writer = new FlvTagWriter("output_mp3.flv");
		writer.prepareHeader(CodecType.MP3);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("mp3.flv")
			),
			writer,
			0,
			1
		);
	}
	/**
	 * to flv(aac)
	 * @throws Exception
	 */
	@Test
	public void flv_aac() throws Exception {
		logger.info("from flv to flv test(aac)");
		FlvTagWriter writer = new FlvTagWriter("output_aac.flv");
		writer.prepareHeader(CodecType.AAC);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("aac.flv")
			),
			writer,
			0,
			1
		);
	}
	/**
	 * to flv(flv1)
	 * @throws Exception
	 */
	@Test
	public void flv_flv1() throws Exception {
		logger.info("from flv to flv test(flv1)");
		FlvTagWriter writer = new FlvTagWriter("output_flv1.flv");
		writer.prepareHeader(CodecType.FLV1);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("flv1.flv")
			),
			writer,
			0,
			1
		);
	}
	/**
	 * to flv(h264)
	 * @throws Exception
	 */
	@Test
	public void flv_h264() throws Exception {
		logger.info("from flv to flv test(h264)");
		FlvTagWriter writer = new FlvTagWriter("output_h264.flv");
		writer.prepareHeader(CodecType.H264);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("h264.flv")
			),
			writer,
			0,
			1
		);
	}
	/**
	 * convert process body.
	 * @param source
	 * @param writer
	 */
	private void convertTest(IFileReadChannel source, IWriter writer, int videoId, int audioId) {
		try {
			// write header
			writer.prepareHeader();
			IReader reader = new FlvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof VideoTag) {
					VideoTag vTag = (VideoTag)container;
					writer.addFrame(videoId, vTag.getFrame());
				}
				else if(container instanceof AudioTag) {
					AudioTag aTag = (AudioTag)container;
					writer.addFrame(audioId, aTag.getFrame());
				}
			}
			// write tailer
			writer.prepareTailer();
		}
		catch(Exception e) {
			logger.error(e);
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
		}
	}
}
