package com.ttProject.container.mpegts;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.frame.IFrame;
import com.ttProject.util.HexUtil;

/**
 * mpegtsのpacketを書き込む動作
 * とりあえずsdt pat pmtは保持しておく。
 * 上記データはkeyFrameがくるもしくは、音声packetの一定秒数ごとに書き出すことにする(もちろんccコントロールも実行しないとだめ)
 * 
 * 音声のみと動画ありとで動作を変更する必要がある。
 * 音声のみの場合は１秒ごとにpes化する形にする。
 * 映像のみの場合は各フレームごとにpes化することになる。
 * それぞれが独立して動作してよいと思う(chunkの場合は合わせる必要があるけど・・・)
 * @author taktod
 */
public class MpegtsPacketWriter implements IWriter {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsPacketWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	
	/** 巡回カウンターマップ */
	private Map<Integer, Integer> continuityCounterMap = new HashMap<Integer, Integer>();
	/** sdtデータ */
	private Sdt sdt = null;
	/** patデータ */
	private Pat pat = null;
	/** pmtデータ */
	private Pmt pmt = null;
	/**
	 * コンストラクタ
	 * @param fileName
	 * @throws Exception
	 */
	public MpegtsPacketWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public MpegtsPacketWriter(FileOutputStream fileOutputStream) {
		this.outputChannel = fileOutputStream.getChannel();
	}
	public MpegtsPacketWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	@Override
	public void addContainer(IContainer container) throws Exception {
		// Containerがはじめて役にたつのかw
		logger.info(container);
		if(container instanceof Sdt) {
			sdt = (Sdt)container;
		}
		else if(container instanceof Pat) {
			pat = (Pat)container;
		}
		else if(container instanceof Pmt) {
			pmt = (Pmt)container;
		}
		logger.info(HexUtil.toHex(container.getData(), true));
	}
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		// pesにデータを当てはめていく必要がある。
		// 初データである場合はsdt、pat、pmtを書き込む
//		logger.info(frame.getPts());
	}
	@Override
	public void prepareHeader() throws Exception {

	}
	@Override
	public void prepareTailer() throws Exception {
		// のこっているpesデータはすべて書き込む
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {}
			outputStream = null;
		}
	}
}
