/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.RiffCodecType;
import com.ttProject.container.riff.RiffUnit;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrameAnalyzer;
import com.ttProject.frame.pcmalaw.PcmalawFrameAnalyzer;
import com.ttProject.frame.pcmmulaw.PcmmulawFrameAnalyzer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.util.BufferUtil;

/**
 * fmt
 * @author taktod
 */
public class Fmt extends RiffUnit {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Fmt.class);
	private Bit16 pcmType       = new Bit16();
	private Bit16 channels      = new Bit16();
	private Bit32 sampleRate    = new Bit32();
	private Bit32 dataSpeed     = new Bit32(); // byteRate byte / sec
	private Bit16 blockSize     = new Bit16();
	private Bit16 bitNum        = new Bit16();
	private Bit16 extraInfoSize = new Bit16();
	@SuppressWarnings("unused")
	private ByteBuffer extraInfo = null;
	/** frame Analyzer */
	private IAnalyzer frameAnalyzer = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(pcmType, channels, sampleRate, dataSpeed, blockSize, bitNum, extraInfoSize);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		extraInfo = BufferUtil.safeRead(channel, extraInfoSize.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
	/**
	 * ref RiffCodecType
	 * @return
	 */
	public RiffCodecType getRiffCodecType() {
		return RiffCodecType.getCodec(pcmType.get());
	}
	/**
	 * ref codecType
	 * @return
	 */
	public CodecType getCodecType() {
		return RiffCodecType.getCodec(pcmType.get()).getCodecType();
	}
	/**
	 * ref analyzer
	 * @return
	 */
	public IAnalyzer getFrameAnalyzer() {
		if(frameAnalyzer != null) {
			return frameAnalyzer;
		}
		switch(getRiffCodecType()) {
		case IMA_ADPCM:
			frameAnalyzer = new AdpcmImaWavFrameAnalyzer();
			break;
		case A_LAW:
			frameAnalyzer = new PcmalawFrameAnalyzer();
			break;
		case U_LAW:
			frameAnalyzer = new PcmmulawFrameAnalyzer();
			break;
		default:
			throw new RuntimeException("codec is unknown:.");
		}
		if(frameAnalyzer instanceof AudioAnalyzer) {
			AudioSelector selector = ((AudioAnalyzer)frameAnalyzer).getSelector();
			selector.setBit(bitNum.get());
			selector.setChannel(channels.get());
			selector.setSampleRate(sampleRate.get());
		}
		return frameAnalyzer;
	}
	public int getBlockSize() {
		return blockSize.get();
	}
}
