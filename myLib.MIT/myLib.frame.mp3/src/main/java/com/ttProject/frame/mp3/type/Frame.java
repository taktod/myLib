/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mp3.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit11;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.util.BufferUtil;

/**
 * mp3 frame.
 * @author taktod
 */
public class Frame extends Mp3Frame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	private Bit11 syncBit           = new Bit11();
	private Bit2  mpegVersion       = new Bit2();
	private Bit2  layer             = new Bit2();
	private Bit1  protectionBit     = new Bit1();
	private Bit4  bitrateIndex      = new Bit4();
	private Bit2  samplingRateIndex = new Bit2();
	private Bit1  paddingBit        = new Bit1();
	private Bit1  privateBit        = new Bit1();
	private Bit2  channelMode       = new Bit2();
	private Bit2  modeExtension     = new Bit2();
	private Bit1  copyRight         = new Bit1();
	private Bit1  originalFlag      = new Bit1();
	private Bit2  emphasis          = new Bit2();
	
	private ByteBuffer rawBuffer;
	
	/** tables */
	private final int bitrateIndexV1L1[] = {
		-1, 32000, 64000, 96000, 128000, 160000, 192000, 224000, 256000, 288000, 320000, 352000, 384000, 416000, 448000, -1
	};
	private final int bitrateIndexV1L2[] = {
		-1, 32000, 48000, 56000,  64000,  80000,  96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 384000, -1
	};
	private final int bitrateIndexV1L3[] = {
		-1, 32000, 40000, 48000,  56000,  64000,  80000,  96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, -1
	};
	private final int bitrateIndexV2L1[] = {
		-1, 32000, 48000, 56000,  64000,  80000,  96000, 112000, 128000, 144000, 160000, 176000, 192000, 224000, 256000, -1
	};
	private final int bitrateIndexV2L23[] = {
		-1,  8000, 16000, 24000,  32000,  40000,  48000,  56000,  64000,  80000,  96000, 112000, 128000, 144000, 160000, -1
	};
	private final int sampleRateTable[][] = {
		{11025, 12000,  8000}, // mpeg 2.5
		{   -1, -1000, -1000},   // reserved
		{22050, 24000, 16000}, // mpeg 2
		{44100, 48000, 32000}  // mpeg 1
	};
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setReadPosition(channel.position() - 1);
		Bit3 syncBit = new Bit3();
		BitLoader loader = new BitLoader(channel);
		loader.load(syncBit, mpegVersion, layer, protectionBit, bitrateIndex, 
				samplingRateIndex, paddingBit, privateBit, channelMode,
				modeExtension, copyRight, originalFlag, emphasis);
		this.syncBit.set(0xFF << 3 | syncBit.get());
		super.setSampleRate(sampleRateTable[mpegVersion.get()][samplingRateIndex.get()]);
		setSampleNum();
		super.setChannel(channelMode.get() == 3 ? 1 : 2);
		setSize();
		super.update();
	}
	/**
	 * set sampleNum
	 * @throws Exception
	 */
	private void setSampleNum() throws Exception {
		switch(layer.get()) {
		case 3: // layer1
			setSampleNum(384);
			break;
		case 2: // layer2
			setSampleNum(1152);
			break;
		case 1: // layer3
			if(mpegVersion.get() == 3) {
				// mpeg1
				setSampleNum(1152);
			}
			else {
				// mpeg2 or mpeg2.5
				setSampleNum(576);
			}
			break;
		default:
			throw new Exception("value of layse is corrupt:" + layer.get());
		}
	}
	/**
	 * bitrate
	 * @return
	 */
	public int getBitrate() {
		if(mpegVersion.get() == 0 || mpegVersion.get() == 2) { // 2.5と2の場合
			if(layer.get() == 3) { // layer1
				return bitrateIndexV2L1[bitrateIndex.get()];
			}
			else if(layer.get() == 2 || layer.get() == 1) { // layer2,3
				return bitrateIndexV2L23[bitrateIndex.get()];
			}
		}
		if(mpegVersion.get() == 3) { // 1の場合
			if(layer.get() == 1) { // layer3
				return bitrateIndexV1L3[bitrateIndex.get()];
			}
			else if(layer.get() == 2) { // layer2
				return bitrateIndexV1L2[bitrateIndex.get()];
			}
			else if(layer.get() == 3) { // layer1
				return bitrateIndexV1L1[bitrateIndex.get()];
			}
		}
		return -1;
	}
	/**
	 * データサイズ計算
	 */
	private void setSize() {
		if(layer.get() == 3) { // layer1
			super.setSize((int)Math.floor((12f * getBitrate() / getSampleRate() + paddingBit.get()) * 4));
		}
		else if(layer.get() == 2) { // layer2
			super.setSize((int)Math.floor(144f * getBitrate() / getSampleRate() + paddingBit.get()));
		}
		else if(layer.get() == 1) { // layer3
			if(mpegVersion.get() == 3) { // version1の場合
				super.setSize((int)Math.floor(144f * getBitrate() / getSampleRate() + paddingBit.get()));
			}
			else {
				super.setSize((int)Math.floor(72f * getBitrate() / getSampleRate() + paddingBit.get()));
			}
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// 本体データも読み込む
		channel.position(getReadPosition() + 4);
		rawBuffer = BufferUtil.safeRead(channel, getSize() - 4);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(rawBuffer == null) {
			throw new Exception("rawBufferが読み込まれていません");
		}
		BitConnector connector = new BitConnector();
		super.setData(BufferUtil.connect(
				connector.connect(syncBit, mpegVersion, layer, protectionBit,
						bitrateIndex, samplingRateIndex, paddingBit, privateBit,
						channelMode, modeExtension, copyRight, originalFlag, emphasis),
				rawBuffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
}
