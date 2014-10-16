/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.RiffFormatUnit;
import com.ttProject.container.riff.StrhRiffCodecType;
import com.ttProject.container.riff.Type;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.mjpeg.MjpegFrameAnalyzer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.util.BufferUtil;

/**
 * strf
 * @author taktod
 */
public class Strf extends RiffFormatUnit {
	private Logger logger = Logger.getLogger(Strf.class);
	// need to inplements bmpInfo?
	private Bit32 biSize = new Bit32();
	private Bit32 biWidth = new Bit32();
	private Bit32 biHeight = new Bit32();
	private Bit16 biPlanes = new Bit16();
	private Bit16 biBitCount = new Bit16();
	private Bit32 biCompression = new Bit32(); // is this fourCC?
	private Bit32 biSizeImage = new Bit32();
	private Bit32 biXPelsPerMeter = new Bit32();
	private Bit32 biYPelsPerMeter = new Bit32();
	private Bit32 biClrUsed = new Bit32();
	private Bit32 biClrImportant = new Bit32();
	
	private StrhRiffCodecType riffCodecType = null;
	private IAnalyzer frameAnalyzer = null;
	private ByteBuffer extraInfo = null;
	// for h264 after this, here is the codecPrivate(configData.)
	// for theora, something wierd here...
	/*
	 * note:these size information in big endian.
	 * 00 2A size
	 * 80 74 .... theora header frame.
	 * 00 3A size
	 * 81 74 .... theora comment frame.
	 * 0C 84 size
	 * 82 74 .... theora setup frame.
	 */
	/**
	 * constructor
	 */
	public Strf(StrhRiffCodecType codecType) {
		super(Type.strf);
		this.riffCodecType = codecType;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(biSize, biWidth, biHeight, biPlanes, biBitCount, biCompression,
				biSizeImage, biXPelsPerMeter, biYPelsPerMeter, biClrUsed, biClrImportant);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		logger.info(getSize());
		if(getSize() - 48 > 0) {
			// TODO here try to read all of the file.
			extraInfo = BufferUtil.safeRead(channel, getSize() - 48);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return riffCodecType.getCodecType();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAnalyzer getFrameAnalyzer() {
		if(frameAnalyzer != null) {
			return frameAnalyzer;
		}
		switch(riffCodecType) {
		case MJPG:
			frameAnalyzer = new MjpegFrameAnalyzer();
			break;
		default:
			throw new RuntimeException("analyzer is unknown");
		}
		if(frameAnalyzer instanceof VideoAnalyzer) {
			VideoSelector selector = ((VideoAnalyzer) frameAnalyzer).getSelector();
			selector.setWidth(biWidth.get());
			selector.setHeight(biHeight.get());
		}
		return frameAnalyzer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBlockSize() {
		return 0;
	}
	/**
	 * ref the extra Info(codecPrivate for theora, )
	 * @return
	 */
	@Override
	public ByteBuffer getExtraInfo() {
		return extraInfo;
	}
}

