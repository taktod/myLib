/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.aac;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.Data;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit7;

/**
 * aac decoderSpecificInfo
 * @see http://wiki.multimedia.cx/index.php?title=MPEG-4_Audio
 * NOTE this is kind of global header.
 * @author taktod
 */
public class DecoderSpecificInfo extends Data {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(DecoderSpecificInfo.class);
	private Bit5  objectType1          = new Bit5(); // profile
	private Bit6  objectType2          = null; // in the case of objectType1 = 31
	private Bit4  frequencyIndex       = new Bit4(); // samplingFrequenceIndex
	private Bit24 frequency            = null; // index is more than 15.
	private Bit4  channelConfiguration = new Bit4();
	private Bit   fillBit              = null;
	// I need more information.
//	private Bit1 frameLengthFlag = new Bit1(); // 0:each packetcontains 1024 samples 1:960 samples
//	private Bit1 dependsOnCoreCoder = new Bit1();
//	private Bit1 extensionFlag = new Bit1();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		int position = channel.position();
		BitLoader loader = new BitLoader(channel);
		loader.load(objectType1);
		if(objectType1.get() == 31) {
			objectType2 = new Bit6();
			loader.load(objectType2);
		}
		loader.load(frequencyIndex);
		if(frequencyIndex.get() == 15) {
			frequency = new Bit24();
			loader.load(frequency);
		}
		loader.load(channelConfiguration);
		fillBit = loader.getExtraBit();
		super.setSize(channel.position() - position);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		int bitCount = objectType1.getBitCount() + (objectType2 != null ? objectType2.getBitCount() : 0)
				+ frequencyIndex.getBitCount() + (frequency != null ? frequency.getBitCount() : 0)
				+ channelConfiguration.getBitCount();
		switch(8 - bitCount % 8) {
		case 1:
			fillBit = new Bit1();
			break;
		case 2:
			fillBit = new Bit2();
			break;
		case 3:
			fillBit = new Bit3();
			break;
		case 4:
			fillBit = new Bit4();
			break;
		case 5:
			fillBit = new Bit5();
			break;
		case 6:
			fillBit = new Bit6();
			break;
		case 7:
			fillBit = new Bit7();
			break;
		default:
			break;
		}
		super.setData(connector.connect(objectType1, objectType2, frequencyIndex,
				frequency, channelConfiguration, fillBit));
	}
	/**
	 * ref objectType
	 * @return
	 */
	public int getObjectType() {
		if(objectType1.get() == 31) {
			return objectType2.get();
		}
		return objectType1.get();
	}
	/**
	 * ref frequencyIndex
	 * @return
	 */
	public int getFrequencyIndex() {
		if(frequencyIndex.get() == 15) {
			return frequency.get();
		}
		return frequencyIndex.get();
	}
	/**
	 * ref channel
	 * @return
	 */
	public int getChannelConfiguration() {
		return channelConfiguration.get();
	}
	public void setObjectType(int type) {
		if(type > 30) {
			objectType1.set(31);
			objectType2 = new Bit6(type);
		}
		else {
			objectType1.set(type);
			objectType2 = null;
		}
		super.update();
	}
	public void setFrequencyIndex(int index, int frequencyNum) {
		if(index > 14) {
			frequencyIndex.set(15);
			frequency = new Bit24(frequencyNum);
		}
		else {
			frequencyIndex.set(index);
			frequency = null;
		}
		super.update();
	}
	public void setChannelConfiguration(int channelConfig) {
		channelConfiguration.set(channelConfig);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder("decoderSpecificInfo:");
		data.append(" ot:").append(objectType1);
		data.append(" fi:").append(frequencyIndex);
		data.append(" cc:").append(channelConfiguration);
		return data.toString();
	}
}
