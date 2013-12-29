package com.ttProject.container.mpegts;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pes;
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
	/** 処理中pesMap */
	private Map<Integer, Pes> pesMap = new HashMap<Integer, Pes>();
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
		if(pesMap.size() == 0) {
			// データがまだない場合は、はじめてのデータなので、sdt, pat, pmtを書き込む必要があります。
			if(sdt == null || pat == null || pmt == null) {
				throw new Exception("必要な情報がありません。");
			}
			logger.info("writeSdt");
			writeMpegtsPacket(sdt);
			logger.info("writePat");
			writeMpegtsPacket(pat);
			logger.info("writePmt");
			writeMpegtsPacket(pmt);
		}
		Pes pes = pesMap.get(trackId);
		if(pes == null) {
			pes = new Pes(trackId, pmt.getPcrPid() == trackId);
			pesMap.put(trackId, pes);
		}
		// pesにデータを当てはめていく必要がある。
		pes.addFrame(frame);
		// pesの中身のデータ量がある程度以上になったらpes完了なので、一旦データを破棄しなければだめ。
	}
	private void writeMpegtsPacket(MpegtsPacket packet) throws Exception {
		Integer counter = continuityCounterMap.get(packet.getPid());
		if(counter == null) {
			counter = 0;
		}
		packet.setContinuityCounter(counter);
		outputChannel.write(packet.getData());
		continuityCounterMap.put(packet.getPid(), counter + 1);
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
