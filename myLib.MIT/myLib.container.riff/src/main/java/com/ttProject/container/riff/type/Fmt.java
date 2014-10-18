/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.WaveFormatExCodecType;
import com.ttProject.container.riff.RiffFormatUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.aac.AacDsiFrameAnalyzer;
import com.ttProject.frame.aac.AacFrameAnalyzer;
import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrameAnalyzer;
import com.ttProject.frame.mp3.Mp3FrameAnalyzer;
import com.ttProject.frame.pcmalaw.PcmalawFrameAnalyzer;
import com.ttProject.frame.pcmmulaw.PcmmulawFrameAnalyzer;
import com.ttProject.frame.vorbis.VorbisFrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.util.BufferUtil;

/**
 * fmt
 * @author taktod
 */
public class Fmt extends RiffFormatUnit {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Fmt.class);
	private Bit16 wFormatTag      = new Bit16();
	private Bit16 nChannels       = new Bit16();
	private Bit32 nSamplePerSec   = new Bit32();
	private Bit32 nAvgBytesPerSec = new Bit32(); // byteRate byte / sec
	private Bit16 nBlockAlign     = new Bit16();
	private Bit16 wBitsPerSample  = new Bit16();
	private Bit16 cbSize          = new Bit16();
	private ByteBuffer extraInfo = null;
	// for the case of vorbis.
	// this extraInfo do have ogg's privateData information.
	/*
	 * 02 1E 54
	 * 2 element
	 * first is 0x1E header
	 * second is 0x54 comment
	 * third is else. setup
	 */
	/** frame Analyzer */
	private IAnalyzer frameAnalyzer = null;
	/**
	 * constructor
	 */
	public Fmt() {
		super(Type.FMT);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(wFormatTag, nChannels, nSamplePerSec, nAvgBytesPerSec, nBlockAlign, wBitsPerSample, cbSize);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		if(cbSize.get() != 0) {
			extraInfo = BufferUtil.safeRead(channel, cbSize.get());
		}
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
	public WaveFormatExCodecType getRiffCodecType() {
		return WaveFormatExCodecType.getCodec(wFormatTag.get());
	}
	/**
	 * ref codecType
	 * @return
	 */
	@Override
	public CodecType getCodecType() {
		return WaveFormatExCodecType.getCodec(wFormatTag.get()).getCodecType();
	}
	/**
	 * ref analyzer
	 * @return
	 */
	@Override
	public IAnalyzer getFrameAnalyzer() throws Exception {
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
		case AAC:
			if(extraInfo == null) {
				frameAnalyzer = new AacFrameAnalyzer();
			}
			else {
				frameAnalyzer = new AacDsiFrameAnalyzer();
				frameAnalyzer.setPrivateData(new ByteReadChannel(extraInfo));
			}
			break;
		case MP3:
			frameAnalyzer = new Mp3FrameAnalyzer();
			break;
		case VORBIS:
			frameAnalyzer = new VorbisFrameAnalyzer();
			frameAnalyzer.setPrivateData(new ByteReadChannel(extraInfo));
			break;
		default:
			throw new RuntimeException("codec is unknown:.");
		}
		if(frameAnalyzer instanceof AudioAnalyzer) {
			AudioSelector selector = ((AudioAnalyzer)frameAnalyzer).getSelector();
			selector.setBit(wBitsPerSample.get());
			selector.setChannel(nChannels.get());
			selector.setSampleRate(nSamplePerSec.get());
		}
		return frameAnalyzer;
	}
	@Override
	public int getBlockSize() {
		return nBlockAlign.get();
	}
	@Override
	public ByteBuffer getExtraInfo() {
		return extraInfo;
	}
}
