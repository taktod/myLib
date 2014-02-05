package com.ttProject.container.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.IWriter;
import com.ttProject.container.flv.FlvHeaderTag;
import com.ttProject.container.flv.FlvTagWriter;
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
 * mpegtsを他のコンテナに変換する動作テスト
 * @author taktod
 */
public class MpegtsToTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsToTest.class);
	/**
	 * flvのh264とaacの動作に変換します。
	 * @throws Exception
	 */
	@Test
	public void flv_h264_aac_ex() throws Exception {
		logger.info("");
		logger.info("flvに変換するテスト(h264)");
		FlvTagWriter writer = new FlvTagWriter("output_mpegts_h264_aac_ex.flv");
		FlvHeaderTag flvHeader = new FlvHeaderTag();
		flvHeader.setAudioFlag(true);
		flvHeader.setVideoFlag(true);
		writer.addContainer(flvHeader);
		convertTest(
			FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/ahiru.ts"
			),
			writer,
			0,
			1
		);
	}
	/**
	 * mpegtsのh264とaacの動作に変換します。
	 * @throws Exception
	 */
	@Test
	public void mpegts_h264_aac_ex() throws Exception {
		logger.info("mpegtsに変換するテスト(h264 / aac)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_mpegts_h264_aac_ex.ts");
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
		PmtElementaryField videoElementaryField = pmtFieldFactory.makeNewField(CodecType.VIDEO_H264);
		pmt.setPcrPid(videoElementaryField.getPid());
		pmt.addNewField(videoElementaryField);
		PmtElementaryField audioElementaryField = pmtFieldFactory.makeNewField(CodecType.AUDIO_AAC);
		pmt.addNewField(audioElementaryField);
		writer.addContainer(pmt);
		// frame追記にあわせてpesを書き込んでいく
		convertTest(
			FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/ahiru.ts"
			),
			writer,
			videoElementaryField.getPid(),
			audioElementaryField.getPid()
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
			IReader reader = new MpegtsPacketReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof Pes) {
					writer.addFrame(((Pes) container).getPid(), ((Pes) container).getFrame());
				}
			}
			for(IContainer rest : reader.getRemainData()) {
				container = rest;
				if(container instanceof Pes) {
					writer.addFrame(((Pes) container).getPid(), ((Pes) container).getFrame());
				}
			}
			writer.prepareTailer();
		}
		catch(Exception e) {
			logger.error("例外発生", e);
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {}
				source = null;
			}
		}
		// tailerを書き込む
	}
}
