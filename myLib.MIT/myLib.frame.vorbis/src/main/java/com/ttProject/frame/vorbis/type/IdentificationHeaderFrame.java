/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit48;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * header frame for vorbis
 * 
 * packetType: 1byte 0x01 identification header
 * string: 6Byte "vorbis"
 * vorbisVersion 32bit integer
 * audioChannels 8bit unsignedInteger
 * audioSampleRate 32bit integer
 * bitrateMaximum 32bit integer
 * bitrateNominal 32bit integer
 * bitrateMinimum 32bit integer
 * blockSize0 2^x 4bit unsigned integer(samples per frame)
 * blockSize1 2^x 4bit unsigned integer(?)
 * framing flag 1bit(actual data is 1 byte.)
 * 
 * @see http://www.xiph.org/vorbis/doc/Vorbis_I_spec.html#x1-620004.2.2
 * @author taktod
 * 
 * sample data.
 * 01 76 6F 72 62 69 73 00 00 00 00 02 44 AC 00 00 FF FF FF FF 00 77 01 00 FF FF FF FF B8 01
 * 
 * identificationHeaderFrame decide the sampleNum too.
 * sampleNum of first frame(blockSize0 + blockSize1) / 4
 * sampleNum of others(blockSize1 + blockSize1) / 4
 */
public class IdentificationHeaderFrame extends VorbisFrame {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(IdentificationHeaderFrame.class);
	private Bit8  packetType      = new Bit8();
	private Bit48 string          = new Bit48();
	private Bit32 vorbisVersion   = new Bit32();
	private Bit8  audioChannels   = new Bit8();
	private Bit32 audioSampleRate = new Bit32();
	private Bit32 bitrateMaximum  = new Bit32();
	private Bit32 bitrateNormal   = new Bit32();
	private Bit32 bitrateMinimum  = new Bit32();
	private Bit4  blockSize0      = new Bit4();
	private Bit4  blockSize1      = new Bit4();
	private Bit1  framingFlag     = new Bit1();
	private Bit   extraBit        = null;

	private boolean isFirstPassed = false;
	private int blockSize0Value = 0;
	private int blockSize1Value = 0;
	
	private CommentHeaderFrame commentHeaderFrame = null;
	private SetupHeaderFrame   setupHeaderFrame   = null;
	
	private ByteBuffer privateBuffer = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(packetType, string, vorbisVersion,
				audioChannels, audioSampleRate,
				bitrateMaximum, bitrateNormal, bitrateMinimum,
				blockSize0, blockSize1, framingFlag);
		extraBit = loader.getExtraBit();
		if(packetType.get() != 1) {
			throw new Exception("packetType value is unexpected.");
		}
		if(string.getLong() != 0x736962726F76L) {
			throw new Exception("string value is different from expected.");
		}
		blockSize0Value = (1 << blockSize0.get());
		blockSize1Value = (1 << blockSize1.get());
		super.setSize(channel.size());
		super.setBit(64);
		super.setChannel(audioChannels.get());
		super.setSampleRate(audioSampleRate.get());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSampleNum() {
		if(!isFirstPassed) {
			isFirstPassed = true;
			return (blockSize0Value + blockSize1Value) / 4;
		}
		else {
			return (blockSize1Value + blockSize1Value) / 4;
		}
	}
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
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		connector.setLittleEndianFlg(true);
		setData(connector.connect(packetType,
				string, vorbisVersion, audioChannels, audioSampleRate, 
				bitrateMaximum, bitrateNormal, bitrateMinimum,
				blockSize0, blockSize1, framingFlag, extraBit));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		if(privateBuffer == null) {
			// return data consists of identificationHeaderFrame + commentHeaderFrame + setupHeaderFrame.
			ByteBuffer identificationData = getData();
			ByteBuffer commentData = commentHeaderFrame.getMinimumBuffer();
			ByteBuffer setupData = setupHeaderFrame.getPackBuffer();
			BitConnector connector = new BitConnector();
			privateBuffer = BufferUtil.connect(
					connector.connect(new Bit8(2),
							new Bit8(identificationData.remaining()),
							new Bit8(commentData.remaining())),
					identificationData,
					commentData,
					setupData);
		}
		return privateBuffer;
	}
	@Override
	public ByteBuffer getPrivateData() throws Exception {
		return getPackBuffer();
	}
	/**
	 * set the CommentHeaderFrame
	 * @param frame
	 */
	public void setCommentHeaderFrame(CommentHeaderFrame frame) {
		this.commentHeaderFrame = frame;
	}
	/**
	 * set the SetupHeaderFrame
	 * @param frame
	 */
	public void setSetupHeaderFrame(SetupHeaderFrame frame) {
		this.setupHeaderFrame = frame;
	}
	// 1つにまとめることができるかとおもったけど、だめっぽい。
	/**
	 * 
	 * @return
	 */
	public CommentHeaderFrame getCommentHeaderFrame() {
		return commentHeaderFrame;
	}
	public SetupHeaderFrame getSetupHeaderFrame() {
		return setupHeaderFrame;
	}
}
