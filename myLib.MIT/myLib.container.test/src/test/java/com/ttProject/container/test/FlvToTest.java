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
 * flvを他のコンテナに変換する動作テスト
 * @author taktod
 */
public class FlvToTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvToTest.class);
	/**
	 * mp3にコンバートする(mp3)
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
	 * adtsにコンバートする(aac)
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
	 * oggにコンバートする(speex)
	 * @throws Exception
	 */
	@Test
	public void ogg() throws Exception {
		OggPageWriter writer = new OggPageWriter("output.ogg");
		logger.info("from flv to ogg test");
		HeaderFrame headerFrame = new HeaderFrame();
		headerFrame.fillWithFlvDefault();
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
	 * mpegtsにコンバートする(h264 aac mp3)
	 * @throws Exception
	 */
	@Test
	public void mpegts_mp3() throws Exception {
		logger.info("from flv to mpegts test(mp3)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_mp3.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// とりあえずsdt pat pmtを設定しなければいけない。
		// sdtを追加
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		// patを追加
		Pat pat = new Pat();
		writer.addContainer(pat);
		// pmtを追加
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField elementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.AUDIO_MPEG1);
		pmt.addNewField(elementaryField);
		pmt.setPcrPid(elementaryField.getPid());
		writer.addContainer(pmt);
		// frame追記にあわせてpesを書き込んでいく
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
	 * mpegtsのaacの変換テスト
	 * @throws Exception
	 */
	@Test
	public void mpegts_aac() throws Exception {
		logger.info("from flv to mpegts test(aac)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_aac.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// とりあえずsdt pat pmtを設定しなければいけない。
		// sdtを追加
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		// patを追加
		Pat pat = new Pat();
		writer.addContainer(pat);
		// pmtを追加
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField elementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.AUDIO_AAC);
		pmt.setPcrPid(elementaryField.getPid());
		pmt.addNewField(elementaryField);
		writer.addContainer(pmt);
		// frame追記にあわせてpesを書き込んでいく
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
	 * mpegtsのh264の変換テスト
	 * @throws Exception
	 */
	@Test
	public void mpegts_h264() throws Exception {
		logger.info("from flv to mpegts test(h264)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_h264.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// とりあえずsdt pat pmtを設定しなければいけない。
		// sdtを追加
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		// patを追加
		Pat pat = new Pat();
		writer.addContainer(pat);
		// pmtを追加
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField elementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.VIDEO_H264);
		pmt.setPcrPid(elementaryField.getPid());
		pmt.addNewField(elementaryField);
		writer.addContainer(pmt);
		// frame追記にあわせてpesを書き込んでいく
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
	 * mpegtsのh264の変換テスト
	 * @throws Exception
	 */
	@Test
	public void mpegts_h264_aac() throws Exception {
		logger.info("from flv to mpegts test(h264 / aac)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_h264_aac.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// とりあえずsdt pat pmtを設定しなければいけない。
		// sdtを追加
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		// patを追加
		Pat pat = new Pat();
		writer.addContainer(pat);
		// pmtを追加
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField videoElementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.VIDEO_H264);
		pmt.setPcrPid(videoElementaryField.getPid());
		pmt.addNewField(videoElementaryField);
		PmtElementaryField audioElementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.AUDIO_AAC);
		pmt.addNewField(audioElementaryField);
		writer.addContainer(pmt);
		// frame追記にあわせてpesを書き込んでいく
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
	 * mpegtsのh264の変換テスト
	 * iPhoneで取った複数sliceで成立するh264のデータ
	 * @throws Exception
	 */
	@Test
	public void mpegts_h264_aac_ex() throws Exception {
		logger.info("from flv to mpegts test(h264 / aac)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_h264_aac_ex.ts");
		PmtElementaryFieldFactory pmtFieldFactory = new PmtElementaryFieldFactory();
		// とりあえずsdt pat pmtを設定しなければいけない。
		// sdtを追加
		Sdt sdt = new Sdt();
		sdt.writeDefaultProvider("test", "hogehoge");
		writer.addContainer(sdt);
		// patを追加
		Pat pat = new Pat();
		writer.addContainer(pat);
		// pmtを追加
		Pmt pmt = new Pmt(pat.getPmtPid());
		PmtElementaryField videoElementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.VIDEO_H264);
		pmt.setPcrPid(videoElementaryField.getPid());
		pmt.addNewField(videoElementaryField);
		PmtElementaryField audioElementaryField = pmtFieldFactory.makeNewField(MpegtsCodecType.AUDIO_AAC);
		pmt.addNewField(audioElementaryField);
		writer.addContainer(pmt);
		// frame追記にあわせてpesを書き込んでいく
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
	 * flvのmp3変換テスト
	 * flvからflameを抜き出して再度flvにします。
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
	 * flvのaac変換テスト
	 * flvからflameを抜き出して再度flvにします。
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
	 * flvのaac変換テスト
	 * flvからflameを抜き出して再度flvにします。
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
	 * flvのaac変換テスト
	 * flvからflameを抜き出して再度flvにします。
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
	 * 内部処理
	 * @param source
	 * @param writer
	 */
	private void convertTest(IFileReadChannel source, IWriter writer, int videoId, int audioId) {
		// headerを書き込む
		try {
			writer.prepareHeader();
			IReader reader = new FlvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof VideoTag) {
					VideoTag vTag = (VideoTag)container;
					// h264のmshの場合はspsとppsがあるので抜き出す必要あり。
					// multiFrameの場合も分解しておくってやったほうがいいか？
					writer.addFrame(videoId, vTag.getFrame());
				}
				else if(container instanceof AudioTag) {
					AudioTag aTag = (AudioTag)container;
					writer.addFrame(audioId, aTag.getFrame());
				}
			}
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
		// tailerを書き込む
	}
}
