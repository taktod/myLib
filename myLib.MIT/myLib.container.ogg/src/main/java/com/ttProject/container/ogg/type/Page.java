/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.ogg.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.ttProject.container.ogg.Crc32;
import com.ttProject.container.ogg.OggPage;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * basic unit of ogg, "page"
 * @see http://www.xiph.org/vorbis/doc/framing.html
 * @see http://ja.wikipedia.org/wiki/Ogg%E3%83%9A%E3%83%BC%E3%82%B8
 * 
 * 内容は次のような感じ
 * pageの開始
 * 4バイト:OggS
 * 1バイト:stream_structure_version (現在は0x00のみ)
 * 1バイト:bitFlag 0000 0abc c:フラグが立っていたら続きpacket b:フラグがたっていたらロジックストリームの開始のページ a:フラグがたっていたらロジックストリームの最後のページ
 * 
 * 8バイト:absoluteGranulePosition 位置情報(含有物次第の値らしい)
 * 4バイト:streamSerialNumber とりあえずなにがしの番号
 * 4バイト:pageSequenceNo ページの番号 mpegtsのcounterみたいなもんかな
 * 4バイト:pageChecksum headerから導くCRC値らしい
 * 1バイト:ページが保持するsegmentsの数
 * 以下セグメントデータ
 *  1バイト:セグメントサイズ(Nとする) ←segmentsの数だけならぶ
 *  Nバイト:セグメント実体 ←segmentsの数だけならぶ
 * みたいな感じになってる。
 * 
 * 以下これの繰り返しっぽい。
 * avconvでつくったoggデータの確認しつつやってみた結果。
 * どこか正しくないことがあっても怒らないこと。
 * 
 * @author taktod
 */
public class Page extends OggPage {
	/** logger */
//	private Logger logger = Logger.getLogger(Page.class);
	/**
	 * constructor
	 * @param version
	 * @param zeroFill
	 * @param logicEndFlag
	 * @param logicStartFlag
	 * @param packetContinurousFlag
	 */
	public Page(Bit8 version,
			Bit1 packetContinurousFlag,
			Bit1 logicStartFlag,
			Bit1 logicEndFlag,
			Bit5 zeroFill) {
		super(version, packetContinurousFlag, logicStartFlag, logicEndFlag, zeroFill);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getPosition() + 27 + getSegmentSizeList().size());
		List<IFrame> frameList = getFrameList();
		int targetSize = 0;
		for(Bit8 size : getSegmentSizeList()) {
			if(size.get() == 0xFF) {
				targetSize += 0xFF;
				continue;
			}
			targetSize += size.get();
			ByteBuffer buffer = BufferUtil.safeRead(channel, targetSize);
			targetSize = 0;
			// 解析したい。
			IReadChannel bufferChannel = new ByteReadChannel(buffer);
			// TODO for other container, load is not the timing for making frame.
			// should I obey them?
			IFrame frame = (IFrame)getStartPage().getAnalyzer().analyze(bufferChannel);
			if(frame instanceof AudioFrame) {
				AudioFrame audioFrame = (AudioFrame) frame;
				audioFrame.setTimebase(audioFrame.getSampleRate());
				audioFrame.setPts(getStartPage().getPassedTic());
				getStartPage().setPassedTic(audioFrame.getPts() + audioFrame.getSampleNum());
			}
			// check frame hashCode, if same, just egnore.(already wrote.)
			if(frameList.size() == 0 || frameList.get(frameList.size() - 1).hashCode() != frame.hashCode()) {
				frameList.add(frame);
			}
		}
		channel.position(getPosition() + getSize());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		ByteBuffer headerBuffer = getHeaderBuffer();
		ByteBuffer buffer = ByteBuffer.allocate(getSize());
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(headerBuffer);
		for(IFrame frame : getFrameList()) {
			ByteBuffer data = frame.getData();
			buffer.put(data);
		}
		ByteBuffer tmpBuffer = buffer.duplicate();
		tmpBuffer.flip();
		// make crc32
		Crc32 crc32 = new Crc32();
		while(tmpBuffer.remaining() > 0) {
			crc32.update(tmpBuffer.get());
		}
		// write crc32
		buffer.position(22);
		buffer.putInt((int)crc32.getValue());
		buffer.position(tmpBuffer.position());
		buffer.flip();
		setData(buffer);
	}
}
