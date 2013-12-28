package com.ttProject.container.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.IWriter;
import com.ttProject.container.adts.AdtsUnitWriter;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.container.mp3.Mp3UnitWriter;
import com.ttProject.container.mpegts.CodecType;
import com.ttProject.container.mpegts.MpegtsPacketWriter;
import com.ttProject.container.mpegts.field.PmtElementaryField;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.container.ogg.OggPageWriter;
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
		logger.info("mp3に変換する動作テスト");
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
//	@Test
	public void adts() throws Exception {
		logger.info("adtsに変換する動作テスト");
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
//	@Test
	public void ogg() throws Exception {
		OggPageWriter writer = new OggPageWriter("output.ogg");
		logger.info("oggに変換する動作テスト");
		HeaderFrame headerFrame = new HeaderFrame();
		headerFrame.fillWithFlvDefault();
		writer.addFrame(1, headerFrame);
		writer.completePage(1);
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
		logger.info("mpegtsに変換するテスト(mp3)");
		MpegtsPacketWriter writer = new MpegtsPacketWriter("output_mp3.ts");
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
		PmtElementaryField elementaryField = PmtElementaryField.makeNewField(CodecType.AUDIO_MPEG1);
		pmt.addNewField(elementaryField);
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
