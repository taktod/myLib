/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;

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
	/** frameからpesを作成 */
	private FrameToPesConverter converter = new FrameToPesConverter();
	/** 初データの処理を実施したかフラグ */
	private boolean isWriteFirstMeta = false;
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
		this(fileOutputStream.getChannel());
	}
	public MpegtsPacketWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	@Override
	public void addContainer(IContainer container) throws Exception {
		// Containerがはじめて役にたつのかw
		if(container instanceof Sdt) {
			sdt = (Sdt)container;
		}
		else if(container instanceof Pat) {
			pat = (Pat)container;
		}
		else if(container instanceof Pmt) {
			pmt = (Pmt)container;
		}
	}
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		if(frame == null) {
			return;
		}
		if(frame instanceof VideoMultiFrame) {
			VideoMultiFrame multiFrame = (VideoMultiFrame) frame;
			for(IVideoFrame vFrame : multiFrame.getFrameList()) {
				addFrame(trackId, vFrame);
			}
			return;
		}
		if(frame instanceof AudioMultiFrame) {
			AudioMultiFrame multiFrame = (AudioMultiFrame) frame;
			for(IAudioFrame aFrame : multiFrame.getFrameList()) {
				addFrame(trackId, aFrame);
			}
			return;
		}
		if(!isWriteFirstMeta) {
			// 初データなので、sdt pat pmtの書き込みが必要です。
			if(sdt == null || pat == null || pmt == null) {
				throw new Exception("必要な情報がありません。");
			}
			writeMpegtsPacket(sdt);
			writeMpegtsPacket(pat);
			writeMpegtsPacket(pmt);
			isWriteFirstMeta = true;
		}
		Pes pes = converter.getPeses(trackId, pmt, frame);
		if(pes == null) {
			return;
		}
		writeMpegtsPacket(pes);
	}
	private void writeMpegtsPacket(MpegtsPacket packet) throws Exception {
		Integer counter = continuityCounterMap.get(packet.getPid());
		if(counter == null) {
			counter = 0;
		}
		packet.setContinuityCounter(counter);
		outputChannel.write(packet.getData());
		continuityCounterMap.put(packet.getPid(), packet.getContinuityCounter() + 1);
	}
	@Override
	public void prepareHeader(CodecType ...codecs) throws Exception {

	}
	@Override
	public void prepareTailer() throws Exception {
		Map<Integer, Pes> remainMap = converter.getPesMap();
		// のこっているpesデータはすべて書き込む
		for(Entry<Integer, Pes> entry : remainMap.entrySet()) {
			// 書き込みしないといけないpesデータ
			Pes pes = entry.getValue();
			try {
				writeMpegtsPacket(pes);
			}
			catch(Exception e) {
				// ここで例外がでるのがそもそもおかしいけど・・・
				logger.error(e);
			}
		}
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {
			}
			outputStream = null;
		}
	}
}
