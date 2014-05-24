/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp3;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.Manager;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.mp3.frame.ID3;
import com.ttProject.media.mp3.frame.Mp3;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * @see http://mpgedit.org/mpgedit/mpeg_format/mpeghdr.htm
 * @author taktod
 */
public class Mp3Manager extends Manager<Frame> {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Mp3Manager.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Frame> getUnits(ByteBuffer data) throws Exception {
		ByteBuffer buffer = appendBuffer(data);
		if(buffer == null) {
			return null;
		}
		IReadChannel bufferChannel = new ByteReadChannel(buffer);
		List<Frame> result = new ArrayList<Frame>();
		while(true) {
			int position = bufferChannel.position();
			Frame frame = getUnit(bufferChannel);
			if(frame == null) {
				// positionを戻します。
				buffer.position(position);
				break;
			}
			frame.analyze(bufferChannel);
			// TODO analyze動作の中身をつくっておきたいところ。
//			logger.info(position);
//			logger.info(frame.getSize());
			bufferChannel.position(position + frame.getSize());
			result.add(frame);
		}
		return result;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Frame getUnit(IReadChannel source) throws Exception {
		if(source.size() - source.position() < 3) {
			// 少なくとも3バイトは必要
			return null;
		}
		int position = source.position();
		ByteBuffer buffer = BufferUtil.safeRead(source, 3);
		byte[] data = new byte[3];
		buffer.get(data);
		// ID3V2の場合
		if(data[0] == 'I'
		&& data[1] == 'D'
		&& data[2] == '3') {
			if(source.size() - position < 10) {
				// 少なくとも10(始めから数えて必要)バイト必要
				return null;
			}
			buffer = BufferUtil.safeRead(source, 7);
			short version = buffer.getShort();
			data = new byte[5];
			buffer.get(data);
			ID3 id3 = new ID3(position, ((data[1] & 0x7F) << 21) + ((data[2] & 0x7F) << 14) + ((data[3] & 0x7F) << 7) + (data[4] & 0x7F) + 10, version, data[0]);
			if(id3.getPosition() + id3.getSize() > source.size()) {
				// 中身を充填するのに必要なサイズがない場合はnullを応答
				return null;
			}
			return id3;
		}
		// ID3V1の場合(終端にくるデータなので、とりあえず無視しておく)
		else if(data[0] == 'T'
		&& data[1] == 'A'
		&& data[2] == 'G') {
			throw new RuntimeException("ID3v1はサポートしていないです。");
		}
		else if(data[0] == (byte)0xFF && (data[1] & 0xE0) == 0xE0) {
			// データが4バイト読み込めない場合は動作不能
			if(source.size() - position < 4) {
				return null;
			}
			byte data3 = BufferUtil.safeRead(source, 1).get();
			IReadChannel byteChannel = new ByteReadChannel(new byte[]{
				data[0], data[1], data[2], data3
			});
			Bit3 syncBit_1 = new Bit3();
			Bit8 syncBit_2 = new Bit8();
			Bit2 mpegVersion = new Bit2();
			Bit2 layer = new Bit2();
			Bit1 protectionBit = new Bit1();
			Bit4 bitrateIndex = new Bit4();
			Bit2 samplingRateIndex = new Bit2();
			Bit1 paddingBit = new Bit1();
			Bit1 privateBit = new Bit1();
			Bit2 channelMode = new Bit2();
			Bit2 modeExtension = new Bit2();
			Bit1 copyRight = new Bit1();
			Bit1 originalFlg = new Bit1();
			Bit2 emphasis = new Bit2();
			BitLoader bitLoader = new BitLoader(byteChannel);
			bitLoader.load(syncBit_1, syncBit_2,
					mpegVersion, layer, protectionBit, bitrateIndex, samplingRateIndex,
					paddingBit, privateBit, channelMode, modeExtension, copyRight, 
					originalFlg, emphasis);
			byteChannel.close();
			Mp3 mp3 = new Mp3(position, mpegVersion, layer, protectionBit, bitrateIndex, samplingRateIndex, paddingBit, privateBit, channelMode, modeExtension, copyRight, originalFlg, emphasis);
			if(mp3.getPosition() + mp3.getSize() > source.size()) {
				return null;
			}
			return mp3;
		}
		return null;
	}

}
