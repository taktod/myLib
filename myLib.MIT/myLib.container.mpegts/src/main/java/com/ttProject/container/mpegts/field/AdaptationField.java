/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.field;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit31;
import com.ttProject.unit.extra.bit.Bit33;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.unit.extra.bit.Bit9;

/**
 * adaptationField
 * @author taktod
 */
public class AdaptationField {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AdaptationField.class);
	private Bit8 adaptationFieldLength  = new Bit8(); // it is possible to have only length.

	private Bit1 discontinuityIndicator = null; // 0
	private Bit1 randomAccessIndicator  = null; // keyFrame flag? aac mp3 pes and keyFrame of h264 will be 1
	private Bit1 elementaryStreamPriorityIndicator = null; // 0
	private Bit1 pcrFlag                           = null;
	private Bit1 opcrFlag                          = null; // originalPcr(in the case of copy use??) // 0
	private Bit1 splicingPointFlag                 = null; // 0
	private Bit1 transportPrivateDataFlag          = null; // 0
	private Bit1 adaptationFieldExtensionFlag      = null; // 0
	// pcr
	private Bit33 pcrBase      = null;
	private Bit6  pcrPadding   = null;
	private Bit9  pcrExtension = null;
	// opcr
	private Bit33 opcrBase      = null;
	private Bit6  opcrPadding   = null;
	private Bit9  opcrExtension = null;
	/**
	 * prepare headerFlag for byte data.
	 */
	private void initElement() {
		if(discontinuityIndicator == null) {
			discontinuityIndicator = new Bit1();
		}
		if(randomAccessIndicator == null) {
			randomAccessIndicator = new Bit1();
		}
		if(elementaryStreamPriorityIndicator == null) {
			elementaryStreamPriorityIndicator = new Bit1();
		}
		if(pcrFlag == null) {
			pcrFlag = new Bit1();
		}
		if(opcrFlag == null) {
			opcrFlag = new Bit1();
		}
		if(splicingPointFlag == null) {
			splicingPointFlag = new Bit1();
		}
		if(transportPrivateDataFlag == null) {
			transportPrivateDataFlag = new Bit1();
		}
		if(adaptationFieldExtensionFlag == null) {
			adaptationFieldExtensionFlag = new Bit1();
		}
	}
	/**
	 * update length
	 */
	private void checkLength() {
		int length = 0;
		if(discontinuityIndicator != null) {
			length += 1;
		}
		if(pcrFlag != null && pcrFlag.get() == 1) {
			length += 6;
		}
		if(opcrFlag != null && opcrFlag.get() == 1) {
			length += 6;
		}
		adaptationFieldLength.set(length);
	}
	public boolean hasPcr() {
		initElement();
		checkLength();
		return pcrFlag.get() == 1;
	}
	public long getPcrBase() {
		BitConnector connector = new BitConnector();
		return connector.connect(new Bit31(), pcrBase).getLong();
	}
	public long getOpcrBase() {
		BitConnector connector = new BitConnector();
		return connector.connect(new Bit31(), opcrBase).getLong();
	}
	public void setPcrFlag(int flag) {
		initElement();
		pcrFlag.set(flag);
		checkLength();
	}
	public void setPcrBase(long base) throws Exception {
		initElement();

		pcrBase = new Bit33();
		pcrPadding = new Bit6(0x3F);
		pcrExtension = new Bit9();
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(base);
		buffer.flip();
		BitLoader loader = new BitLoader(new ByteReadChannel(buffer));
		loader.load(new Bit31(), pcrBase);
		checkLength();
	}
	public void setRandomAccessIndicator(int flg) {
		// adaptationFieldの長さが存在しない場合は1に変更する必要あり。
		initElement();
		randomAccessIndicator = new Bit1(flg);
		checkLength();
	}
	// pcr opcr spliceCountdown stuffingBytes等々・・・
	public void load(IReadChannel channel) throws Exception {
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(adaptationFieldLength);
		if(adaptationFieldLength.get() == 0x00) {
			return;
		}
		int size = adaptationFieldLength.get();
		initElement();
		bitLoader = new BitLoader(channel);
		bitLoader.load(discontinuityIndicator, randomAccessIndicator,
				elementaryStreamPriorityIndicator, pcrFlag, opcrFlag, splicingPointFlag,
				transportPrivateDataFlag, adaptationFieldExtensionFlag);
		size --;
		// load extra data.
		if(pcrFlag.get() != 0x00) {
			// pcr
			// next 33bit 6bit 9bit is the data.
			// 33bit will be duration(timebase = 90000)
			// 6bit is padding bit filled with zero.
			// 9bit is duration(timebase 27M)
			pcrBase = new Bit33();
			pcrPadding = new Bit6();
			pcrExtension = new Bit9();
			bitLoader = new BitLoader(channel);
			bitLoader.load(pcrBase, pcrPadding, pcrExtension);
			size -= 6;
		}
		if(opcrFlag.get() != 0x00) {
			// looks like pcr. just do the same.
			opcrBase = new Bit33();
			opcrPadding = new Bit6();
			opcrExtension = new Bit9();
			bitLoader = new BitLoader(channel);
			bitLoader.load(opcrBase, opcrPadding, opcrExtension);
			size -= 6;
		}
		if(splicingPointFlag.get() != 0x00) {
			throw new Exception("splicingPoint analyzation is not supported yet.");
		}
		if(transportPrivateDataFlag.get() != 0x00) {
			throw new Exception("transportPrivateData analyzation is not supported yet.");
		}
		if(adaptationFieldExtensionFlag.get() != 0x00) {
			throw new Exception("adaptationFieldExtension analyzation is not supported yet.");
		}
		if(size != 0) {
			// fill with empty data(0xff)
			channel.position(channel.position() + size); // skip the data.
		}
	}
	/**
	 * change length
	 * @param length
	 */
	public void setLength(int length) {
		adaptationFieldLength = new Bit8(length);
		initElement();
	}
	/**
	 * ref length
	 * @return
	 */
	public int getLength() {
		return adaptationFieldLength.get();
	}
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		int length = adaptationFieldLength.get();
		list.add(adaptationFieldLength);
		if(length == 0) {
			return list;
		}
		list.add(discontinuityIndicator);
		list.add(randomAccessIndicator);
		list.add(elementaryStreamPriorityIndicator);
		list.add(pcrFlag);
		list.add(opcrFlag);
		list.add(splicingPointFlag);
		list.add(transportPrivateDataFlag);
		list.add(adaptationFieldExtensionFlag);
		length --;
		if(pcrFlag.get() != 0x00) {
			list.add(pcrBase);
			list.add(pcrPadding);
			list.add(pcrExtension);
			length -= 6;
		}
		if(opcrFlag.get() != 0x00) {
			list.add(opcrBase);
			list.add(opcrPadding);
			list.add(opcrExtension);
			length -= 6;
		}
		for(int i = 0;i < length;i ++) {
			list.add(new Bit8((byte)0xFF));
		}
		return list;
	}
	public int getRandomAccessIndicator() {
		initElement();
		checkLength();
		return randomAccessIndicator.get();
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(" ");
		data.append("adaptationField:");
		data.append(" afl:").append(Integer.toHexString(adaptationFieldLength.get()));
		if(adaptationFieldLength.get() != 0) {
			data.append(" di:").append(discontinuityIndicator);
			data.append(" rai:").append(randomAccessIndicator);
			data.append(" espi:").append(elementaryStreamPriorityIndicator);
			data.append(" pf:").append(pcrFlag);
			data.append(" of:").append(opcrFlag);
			data.append(" spf:").append(splicingPointFlag);
			data.append(" tpdf:").append(transportPrivateDataFlag);
			data.append(" afef:").append(adaptationFieldExtensionFlag);
			if(pcrFlag.get() != 0x00) {
				data.append("[pcrBase:").append(Long.toHexString(getPcrBase()))
					.append("(").append(getPcrBase() / 90000f).append("sec)");
				data.append(" pcrPadding:").append(pcrPadding);
				data.append(" pcrExtension:").append(pcrExtension);
				data.append("]");
			}
			if(opcrFlag.get() != 0x00) {
				data.append("[opcrBase:").append(Long.toHexString(getOpcrBase()))
					.append("(").append(getOpcrBase() / 90000f).append("sec)");
				data.append(" opcrPadding:").append(opcrPadding);
				data.append(" opcrExtension:").append(opcrExtension);
				data.append("]");
			}
		}
		return data.toString();
	}
}
