/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.aac;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.Manager;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * ADTS方式であるとして、Aacのデータを解析します。
 * @see http://blog-imgs-18-origin.fc2.com/n/a/n/nanncyatte/aacfileheader.png
 * @author taktod
 */
public class AacManager extends Manager<Frame> {
	/** ロガー */
	private Logger logger = Logger.getLogger(AacManager.class);
	/**
	 * adtsの形式としてデータを取り出します。
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
				buffer.position(position);
				break;
			}
			frame.analyze(bufferChannel);
			logger.info("size:" + bufferChannel.size() + " pos:" + (position + frame.getSize()));
			bufferChannel.position(position + frame.getSize());
			result.add(frame);
		}
		return result;
	}
	@Override
	public Frame getUnit(IReadChannel source) throws Exception {
		// frameUnitを解析します。
		// headerは7バイトで構成されているので、7バイト存在しない場合は処理できません。
		if(source.size() - source.position() < 7) {
			return null;
		}
		int position = source.position();
		Bit4 syncBit_1 = new Bit4();
		Bit8 syncBit_2 = new Bit8();
		Bit1 id = new Bit1();
		Bit2 layer = new Bit2();
		Bit1 protectionAbsent = new Bit1();
		Bit2 profile = new Bit2();
		Bit4 samplingFrequenceIndex = new Bit4();
		Bit1 privateBit = new Bit1();
		Bit3 channelConfiguration = new Bit3();
		Bit1 originalFlg = new Bit1();
		Bit1 home = new Bit1();
		Bit1 copyrightIdentificationBit = new Bit1();
		Bit1 copyrightIdentificationStart = new Bit1();
		Bit5 frameSize_1 = new Bit5();
		Bit8 frameSize_2 = new Bit8();
		Bit3 adtsBufferFullness_1 = new Bit3();
		Bit8 adtsBufferFullness_2 = new Bit8();
		Bit2 noRawDataBlocksInFrame = new Bit2();
		BitLoader bitLoader = new BitLoader(source);
		bitLoader.load(
			syncBit_1, syncBit_2, id, layer, protectionAbsent, profile, samplingFrequenceIndex,
			privateBit, channelConfiguration, originalFlg, home,
			copyrightIdentificationBit, copyrightIdentificationStart, frameSize_1, frameSize_2,
			adtsBufferFullness_1, adtsBufferFullness_2, noRawDataBlocksInFrame);
		int size = (frameSize_1.get() << 8) + frameSize_2.get();
		Aac aac = new Aac(position, size, id, layer, protectionAbsent, profile, samplingFrequenceIndex, privateBit, channelConfiguration, originalFlg, home, copyrightIdentificationBit, copyrightIdentificationStart, size, (adtsBufferFullness_1.get() << 8) + adtsBufferFullness_2.get(), noRawDataBlocksInFrame);
		if(aac.getPosition() + aac.getSize() > source.size()) {
			return null;
		}
		return aac;
	}
}
